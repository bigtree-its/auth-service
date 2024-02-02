package com.bigtree.auth.repository;

import com.bigtree.auth.entity.CustomerPreferences;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerPreferenceRepository extends MongoRepository<CustomerPreferences, String> {

    CustomerPreferences findByCustomerId(String customerId);
}
