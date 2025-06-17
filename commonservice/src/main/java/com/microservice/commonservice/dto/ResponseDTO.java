package com.microservice.commonservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseDTO {
    private final Integer statusCode;
    private final String message;
    private final Object data;
}
