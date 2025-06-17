package com.microservice.authservice.repository;

import com.microservice.authservice.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);

    boolean existsByEmail(String email);
}