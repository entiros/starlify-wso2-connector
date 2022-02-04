package com.entiros.starlify.muleconnector.api.service;

import com.entiros.starlify.muleconnector.api.dto.Request;
import com.entiros.starlify.muleconnector.api.dto.RequestItem;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.ExecutionException;

public interface StarlifyExportService {
    RequestItem status(Request request);

    RequestItem submitWso2Request(Request request);
}
