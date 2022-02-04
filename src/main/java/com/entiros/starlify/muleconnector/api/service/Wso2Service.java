package com.entiros.starlify.muleconnector.api.service;

import com.entiros.starlify.muleconnector.api.dto.wso2.Wso2ApiDetails;
import com.entiros.starlify.muleconnector.api.dto.wso2.Wso2Apis;

public interface Wso2Service {

    Wso2Apis getWso2Apis(String accessToken);

    Wso2ApiDetails getWso2ApiDetails(String accessToken, String apiId);

}
