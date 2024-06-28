package com.bigtree.auth.controller;

import com.bigtree.auth.model.ActivateAccountRequest;
import com.bigtree.auth.model.ApiResponse;
import com.bigtree.auth.model.PasswordResetInitiate;
import com.bigtree.auth.model.PasswordResetSubmit;
import com.bigtree.auth.service.LoginService;
import com.bigtree.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/v1/auth")
public class PasswordController {

    @Autowired
    LoginService loginService;

    @Autowired
    UserService userService;

    @PostMapping(value = "/passwords/reset_initiate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> passwordResetInitiate(@Valid @RequestBody PasswordResetInitiate req){
        log.info("Received password reset initiate request for user {}", req.getEmail());
        loginService.passwordResetInitiate(req.getEmail());
        ApiResponse apiResponse = ApiResponse.builder().message("An OTP sent to your registered email address. Please use that to reset your password").build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PostMapping(value = "/passwords/reset_submit", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> passwordResetSubmit(@Valid @RequestBody PasswordResetSubmit req){
        log.info("Received password reset request for user {}", req.getEmail());
        loginService.passwordResetSubmit(req);
        ApiResponse apiResponse = ApiResponse.builder().message("Your password has been successfully changed.").build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/account_activate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> activateAccount(@Valid @RequestBody ActivateAccountRequest req){
        log.info("Received request to activate account {}", req.getAccountId());
        userService.activateAccount(req);
        ApiResponse apiResponse = ApiResponse.builder().message("Your account has been successfully activated.").build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
