package com.bigtree.auth.controller;

import com.bigtree.auth.model.TokenResponse;
import com.bigtree.auth.service.LoginService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/oauth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    LoginService loginService;

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> token(@RequestHeader("User-Agent") String userAgent, @Valid @RequestParam MultiValueMap form){
        log.info("Received request to token for {} from {}", form.toString(), userAgent);
        TokenResponse status = loginService.token(form);
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }

    @PostMapping(value = "/revoke", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> revoke(@Valid @RequestParam MultiValueMap form){
        log.info("Received request to revoke token {}",form.toString());
        loginService.logout(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
