package com.entiros.starlify.muleconnector.api.dto;

import lombok.Data;

@Data
public class SystemDto {
    private String id;
    private String localId;
    private String name;
    private String description;
    private Network network;
}
