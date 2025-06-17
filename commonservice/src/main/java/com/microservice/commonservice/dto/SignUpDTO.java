package com.microservice.commonservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpDTO {

    private String name;

    private String email;

    private String password;

    private String role;

    private String createdBy;
}
