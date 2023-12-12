package com.bigtree.user.controller;

import com.bigtree.user.model.LoginRequest;
import com.bigtree.user.model.LoginResponse;
import com.bigtree.user.model.LogoutRequest;
import com.bigtree.user.service.LoginService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    LoginService loginService;

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponse> login(@RequestHeader("user-agent") String userAgent, @Valid @RequestBody LoginRequest loginRequest){
        log.info("Received request to login for user {} from {}", loginRequest.getEmail(), userAgent);
        LoginResponse status = loginService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }

    @PostMapping(value = "/logout", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request){
        log.info("Received request to logout user {}", request.getUserId());
        loginService.logout(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
