package com.entiros.starlify.muleconnector.api.service.impl;

import com.entiros.starlify.muleconnector.api.dto.*;
import com.entiros.starlify.muleconnector.api.dto.wso2.EndPointsDetails;
import com.entiros.starlify.muleconnector.api.dto.wso2.Wso2Apis;
import com.entiros.starlify.muleconnector.api.service.StarlifyExportService;
import com.entiros.starlify.muleconnector.api.service.StarlifyService;
import com.entiros.starlify.muleconnector.api.service.Wso2Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class StarlifyExportServiceImpl implements StarlifyExportService {


    private final StarlifyService starlifyService;
    private final Wso2Service wso2Service;

    private Map<String, Map<String, NetworkSystem>> cachedNetworkSystems = new ConcurrentHashMap<>();
    private Map<String, RequestItem> statusMap = new ConcurrentHashMap<>();

    @Override
    public RequestItem status(Request request) {
        return statusMap.get(request.getNetworkId());
    }

    @Override
    public RequestItem submitWso2Request(Request request) {
        RequestItem workItem = new RequestItem();
        workItem.setStatus(RequestItem.Status.NOT_STARTED);
        workItem.setStarlifyKey(request.getStarlifyKey());
        workItem.setApiKey(request.getApiKey());
        workItem.setNetworkId(request.getNetworkId());
        statusMap.put(request.getNetworkId(), workItem);
        CompletableFuture.runAsync(() -> {
            try {
                processWso2Request(workItem);
            } catch (Throwable t) {
                log.error("error while processing request", t);
            }
        });
        return workItem;
    }

    private void processWso2Request(RequestItem request) {
        ((RequestItem) request).setStatus(RequestItem.Status.IN_PROCESS);


        Wso2Apis wso2Apis = wso2Service.getWso2Apis(request.getApiKey());

        List<NetworkSystem> systems = starlifyService.getSystems(request);

        this.populateSystems(request, systems);

        Map<String, NetworkSystem> existingSystems = cachedNetworkSystems.get(request.getNetworkId());
        List<com.entiros.starlify.muleconnector.api.dto.wso2.List> apis = wso2Apis.getList();
        for (com.entiros.starlify.muleconnector.api.dto.wso2.List api : apis) {
            try {
                log.info("Started def:" + api.getName() + " id:" + api.getId());
                NetworkSystem networkSystem = existingSystems != null ? existingSystems.get(api.getName()) : null;
                String systemId = null;
                if (networkSystem == null) {
                    SystemDto systemDto = this.createSystemDto(request, api.getName(), api.getDescription());
                    SystemRespDto systemRespDto = starlifyService.addSystem(request, systemDto);
                    systemId = systemRespDto.getId();
                } else {
                    systemId = networkSystem.getId();
                }
                Response<ServiceRespDto> services = starlifyService.getServices(request, systemId);
                Set<String> serviceNames = this.getServiceNames(services);
                List<EndPointsDetails> apiEndpoints = getWso2EndPoints(api);

                if (apiEndpoints == null || apiEndpoints.isEmpty()) {
                    log.info("empty endpoints size :");
                    continue;
                }
                log.info("Endpoints size :" + apiEndpoints.size());
                for (EndPointsDetails ep : apiEndpoints) {
                    try {
                        if (ep.getPath() == null) {
                            log.info("empty details:");
                            continue;
                        }

                        for (String method : ep.getMethods()) {
                            String name = method + " " + ep.getPath();
                            if (!serviceNames.contains(name)) {
                                ServiceDto dto = new ServiceDto();
                                dto.setName(name);
                                starlifyService.addService(request, dto, systemId);
                            }
                        }
                    } catch (Throwable e) {
                        log.error("Error while processing service:" + api.getName(), e);
                    }
                }
                ((RequestItem) request).setStatus(RequestItem.Status.DONE);
                log.info("Started asset:" + api.getName());
            } catch (Throwable t) {
                log.error("Error while processing asset:" + api.getName(), t);
                ((RequestItem) request).setStatus(RequestItem.Status.ERROR);
            }
        }
    }

    private List<EndPointsDetails> getWso2EndPoints(com.entiros.starlify.muleconnector.api.dto.wso2.List api) throws JsonProcessingException {

        List<EndPointsDetails> endPointsDetails = new ArrayList<>();
        Map<String, Map<String, Object>> map = new ObjectMapper().readValue(api.getWso2ApiDetails().getApiDefinition(), Map.class);
        log.info(map.keySet().toString());
        Map<String, Object> paths = map.get("paths");
        for (String path : paths.keySet()) {
            EndPointsDetails pointsDetails = new EndPointsDetails();
            pointsDetails.setPath(path);
            Map<String, Object> methodsMap = new ObjectMapper().convertValue(paths.get(path), Map.class);
            ArrayList<String> methodsList = new ArrayList<>();
            for (String method : methodsMap.keySet()) {
                methodsList.add(method);
            }
            pointsDetails.setMethods(methodsList);
            endPointsDetails.add(pointsDetails);
        }
        return endPointsDetails;
    }

    private SystemDto createSystemDto(Request request, String name, String description) {
        SystemDto s = new SystemDto();
        String id = UUID.randomUUID().toString();
        s.setId(id);
        s.setName(name);
        Network n = new Network();
        n.setId(request.getNetworkId());
        s.setNetwork(n);
        s.setDescription(description);
        return s;
    }

    private synchronized void populateSystems(Request request, List<NetworkSystem> networkSystems) {
        if (networkSystems != null && !networkSystems.isEmpty()) {
            Map<String, NetworkSystem> existingSystems = cachedNetworkSystems.get(request.getNetworkId());
            if (existingSystems == null) {
                existingSystems = new ConcurrentHashMap<>();
                cachedNetworkSystems.put(request.getNetworkId(), existingSystems);
            }
            for (NetworkSystem ns : networkSystems) {
                existingSystems.put(ns.getName(), ns);
            }
        }
    }

    private synchronized Set<String> getServiceNames(Response<ServiceRespDto> services) {
        List<ServiceRespDto> content = services.getContent();
        Set<String> ret = new HashSet<>();
        if (content != null && !content.isEmpty()) {
            for (ServiceRespDto c : content) {
                ret.add(c.getName());
            }
        }
        return ret;
    }
}
