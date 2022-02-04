package com.entiros.starlify.muleconnector.api.controller;

import com.entiros.starlify.muleconnector.api.dto.*;
import com.entiros.starlify.muleconnector.api.service.StarlifyExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class Wso2Controller {

    private final StarlifyExportService starlifyExportService;

    @PostMapping("/status")
    public RequestItem getStatus(@RequestBody Request request) {
        return starlifyExportService.status(request);
    }

    @PostMapping("/process/wso2")
    public RequestItem processWso2(@RequestBody Request request) throws ExecutionException, InterruptedException {
        log.info("mule key recieved {}", request.getApiKey());
        return starlifyExportService.submitWso2Request(request);
    }

}

