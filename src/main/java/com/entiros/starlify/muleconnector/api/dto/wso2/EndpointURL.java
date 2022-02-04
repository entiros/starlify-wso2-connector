package com.entiros.starlify.muleconnector.api.dto.wso2;


import lombok.Data;

@Data
public class EndpointURL{
    public String environmentName;
    public String environmentType;
    public EnvironmentURLs environmentURLs;
}