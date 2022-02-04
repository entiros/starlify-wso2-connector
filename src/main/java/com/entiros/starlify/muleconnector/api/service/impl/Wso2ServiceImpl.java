package com.entiros.starlify.muleconnector.api.service.impl;


import com.entiros.starlify.muleconnector.api.dto.Asset;
import com.entiros.starlify.muleconnector.api.dto.wso2.List;
import com.entiros.starlify.muleconnector.api.dto.wso2.Wso2ApiDetails;
import com.entiros.starlify.muleconnector.api.dto.wso2.Wso2Apis;
import com.entiros.starlify.muleconnector.api.service.Wso2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class Wso2ServiceImpl implements Wso2Service {


    private final RestTemplate restTemplate;

    @Value("${wso2.server.url}")
    private String apiServer;


    @Override
    public Wso2Apis getWso2Apis(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<Wso2Apis> response = restTemplate.exchange(apiServer + "/store/apis",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<Wso2Apis>() {
                });
        Wso2Apis wso2Apis = response.getBody();

        for (List api : wso2Apis.getList()) {
            api.setWso2ApiDetails(getWso2ApiDetails(accessToken, api.getId()));
        }
        return wso2Apis;
    }

    @Override
    public Wso2ApiDetails getWso2ApiDetails(String accessToken, String apiId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<Wso2ApiDetails> response = restTemplate.exchange(apiServer + "/store/apis/" + apiId,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<Wso2ApiDetails>() {
                });
        Wso2ApiDetails wso2ApiDetails = response.getBody();
        return wso2ApiDetails;
    }
}
