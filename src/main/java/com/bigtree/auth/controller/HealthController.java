package com.bigtree.auth.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@Slf4j
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class HealthController {
    
    @GetMapping("")
    public String getMethodName() {
        log.info("Status check");
        return "Ok";
    }
    

}
