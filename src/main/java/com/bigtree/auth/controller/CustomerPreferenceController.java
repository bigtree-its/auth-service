package com.bigtree.auth.controller;

import com.bigtree.auth.entity.CustomerPreferences;
import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.security.JwtService;
import com.bigtree.auth.service.CustomerPreferenceService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping("/api/customers")
@CrossOrigin
public class CustomerPreferenceController {

    @Autowired
    CustomerPreferenceService customerPreferenceService;

    @GetMapping("/{customerId}/preferences")
    public ResponseEntity<CustomerPreferences> get(@PathVariable String customerId){
        log.info("Received request to get customer preference for {}", customerId);
        CustomerPreferences customerPreferences = customerPreferenceService.get(customerId);
        return ResponseEntity.ok().body(customerPreferences);
    }

    @PutMapping(value = "/preferences", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerPreferences> updatePreferences(@RequestBody CustomerPreferences customerPreferences){
        log.info("Received request to update customer preference for {}", customerPreferences.getCustomerId());
        CustomerPreferences preferences = customerPreferenceService.createOrUpdate(customerPreferences);
        return ResponseEntity.status(HttpStatus.OK).body(preferences);
    }

    @PostMapping(value = "/preferences", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerPreferences> createPreferences(@RequestBody CustomerPreferences customerPreferences){
        log.info("Received request to update customer preference for {}", customerPreferences.getCustomerId());
        CustomerPreferences preferences = customerPreferenceService.createOrUpdate(customerPreferences);
        return ResponseEntity.status(HttpStatus.OK).body(preferences);
    }

    @DeleteMapping("/{customerId}/preferences")
    public ResponseEntity<CustomerPreferences> delete(@PathVariable String customerId){
        log.info("Received request to delete customer preference for {}", customerId);
        customerPreferenceService.delete(customerId);
        return ResponseEntity.ok().build();
    }
}
