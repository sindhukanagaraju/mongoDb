package com.microservice.organizationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "organizations")  // MongoDB collection name
public class Organization {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String contactNumber;

    private String industryType;
}
