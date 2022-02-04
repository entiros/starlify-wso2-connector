package com.entiros.starlify.muleconnector.api.dto.wso2;

import lombok.Data;

import java.util.ArrayList;

@Data
public class EndPointsDetails {
    String path;
    ArrayList<String> methods;
}
