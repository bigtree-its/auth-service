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
@CrossOrigin(origins = "*")
public class CustomerPreferenceController {

    @Autowired
    CustomerPreferenceService customerPreferenceService;

    @Autowired
    JwtService jwtService;

    @GetMapping("/{customerId}/preferences")
    public ResponseEntity<CustomerPreferences> get(@PathVariable String customerId, @RequestHeader("Authorization") String token){
        log.info("Received request to get customer preference for {}", customerId);
        jwtService.authenticate(customerId, token);
        CustomerPreferences customerPreferences = customerPreferenceService.get(customerId);
        return ResponseEntity.ok().body(customerPreferences);
    }

    @PutMapping(value = "/{customerId}/preferences", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerPreferences> updatePreferences(@PathVariable String customerId, @RequestHeader("Authorization") String authorization, @RequestBody CustomerPreferences customerPreferences){
        log.info("Received request to update customer preference for {}", customerId);
        CustomerPreferences preferences = customerPreferenceService.update(customerId, customerPreferences);
        return ResponseEntity.status(HttpStatus.OK).body(preferences);
    }

    @PostMapping(value = "/{customerId}/preferences", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerPreferences> createPreferences(@PathVariable String customerId, @RequestHeader("Authorization") String authorization, @RequestBody CustomerPreferences customerPreferences){
        log.info("Received request to update customer preference for {}", customerId);
        CustomerPreferences preferences = customerPreferenceService.create(customerId, customerPreferences);
        return ResponseEntity.status(HttpStatus.OK).body(preferences);
    }
}
