package com.microservice.organizationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.microservice.organizationservice", "com.microservice.commonservice"})
public class OrganizationserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganizationserviceApplication.class, args);
    }

}
