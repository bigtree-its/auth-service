package com.bigtree.auth.service;

import com.bigtree.auth.entity.CustomerPreferences;
import com.bigtree.auth.entity.User;
import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.repository.CustomerPreferenceRepository;
import com.bigtree.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CustomerPreferenceService {

    @Autowired
    CustomerPreferenceRepository repository;

    @Autowired
    UserRepository userRepository;

    public CustomerPreferences get(String customerId) {
        return repository.findByCustomerId(customerId);
    }


    public CustomerPreferences createOrUpdate(CustomerPreferences customerPreferences) {
        Optional<User> byId = userRepository.findById(customerPreferences.getCustomerId());
        if (byId.isPresent()) {
            CustomerPreferences existing = repository.findByCustomerId(customerPreferences.getCustomerId());
            if (existing != null) {
                log.info("Customer preference already exist. Updating");
                if (customerPreferences.getCommunicationViaEmail() != null) {
                    existing.setCommunicationViaEmail(customerPreferences.getCommunicationViaEmail());
                }
                if (customerPreferences.getCommunicationViaMobile() != null) {
                    existing.setCommunicationViaMobile(customerPreferences.getCommunicationViaMobile());
                }
                if (customerPreferences.getChefs() != null) {
                    existing.setChefs(customerPreferences.getChefs());
                }
                if (customerPreferences.getCuisines() != null) {
                    existing.setCuisines(customerPreferences.getCuisines());
                }
                if (customerPreferences.getFoods() != null) {
                    existing.setFoods(customerPreferences.getFoods());
                }
                return repository.save(existing);
            } else {
                log.info("Creating Customer preference");
                return repository.save(customerPreferences);
            }
        }
        log.error("Customer not found. Cannot update preferences");
        throw new ApiException(HttpStatus.BAD_REQUEST, "Customer not found. Cannot update preferences");
    }

    public void delete(String customerId) {
        Optional<User> byId = userRepository.findById(customerId);
        if (byId.isPresent()) {
            repository.deleteByCustomerId(customerId);
            return;
        }
        log.error("Customer not found. Cannot update preferences");
        throw new ApiException(HttpStatus.BAD_REQUEST, "Customer not found. Cannot update preferences");
    }
}
