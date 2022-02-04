package com.entiros.starlify.muleconnector.api.dto.wso2;


import lombok.Data;

@Data
public class Pagination{
    public int total;
    public int offset;
    public int limit;
}