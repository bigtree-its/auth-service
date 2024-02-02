package com.bigtree.auth.service;

import com.bigtree.auth.entity.ClientType;
import com.bigtree.auth.entity.CustomerPreferences;
import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.repository.CustomerPreferenceRepository;
import com.bigtree.auth.repository.IdentityRepository;
import com.bigtree.auth.security.JwtService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CustomerPreferenceService {

    @Autowired
    CustomerPreferenceRepository repository;

    @Autowired
    IdentityRepository identityRepository;

    public CustomerPreferences get(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    public CustomerPreferences update(String customerId, CustomerPreferences customerPreferences) {
        Optional<Identity> byId = identityRepository.findById(customerId);
        if (byId.isPresent()) {
            log.info("Updating Customer preference");
            return repository.save(customerPreferences);
        }
        return customerPreferences;
    }

    public CustomerPreferences create(String customerId, CustomerPreferences customerPreferences) {
        Optional<Identity> byId = identityRepository.findById(customerId);
        if (byId.isPresent()) {
            log.info("Customer preference already exist. Updating");
            return update(customerId, customerPreferences);
        }else{
            log.info("Creating Customer preference");
            return repository.save(customerPreferences);
        }
    }
}
