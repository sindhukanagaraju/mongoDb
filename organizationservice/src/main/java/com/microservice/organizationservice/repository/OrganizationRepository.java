package com.microservice.organizationservice.repository;

import com.microservice.organizationservice.entity.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends MongoRepository<Organization, String> {

    Organization findByEmail(String email);
}

