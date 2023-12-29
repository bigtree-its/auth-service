package com.bigtree.auth.controller;

import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.entity.Session;
import com.bigtree.auth.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.model.*;
import com.bigtree.auth.service.LoginService;
import com.bigtree.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    LoginService loginService;

    @Autowired
    SessionService sessionService;

    @GetMapping("")
    public ResponseEntity<List<Identity>> getAll(){
        log.info("Received request to get all identities");
        List<Identity> identities = userService.getUsers();
        return ResponseEntity.ok().body(identities);
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<Identity> get(@PathVariable String userId){
        log.info("Received request to get auth {}", userId);
        Identity identity = userService.getUser(userId);
        return ResponseEntity.ok().body(identity);
    }

    @PostMapping(value = "/password-reset/initiate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> passwordResetInitiate(@Valid @RequestBody PasswordResetInitiate req){
        log.info("Received password reset initiate request for auth {}", req.getEmail());
        loginService.passwordResetInitiate(req.getEmail());
        ApiResponse apiResponse = ApiResponse.builder().message("An OTP sent to your registered email address. Please use that to reset your password").build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PostMapping(value = "/password-reset/submit", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> passwordResetSubmit(@Valid @RequestBody PasswordResetSubmit req){
        log.info("Received password reset request for auth {}", req.getEmail());
        loginService.passwordResetSubmit(req);
        ApiResponse apiResponse = ApiResponse.builder().message("Your password has been successfully changed.").build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest){
        log.info("Received request to register new auth {}", userRegistrationRequest);
        ApiResponse response  = userService.registerUser(userRegistrationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping(value = "/{userId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Identity> update(@PathVariable String userId, @Valid @RequestBody Identity identity){
        log.info("Received request to update auth {}", userId);
        Identity updated = userService.updateUser(userId, identity);
        return ResponseEntity.ok().body(updated);
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> delete(@PathVariable String userId){
        log.info("Received request to delete auth {}", userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "")
    public ResponseEntity<Void> deleteAll(){
        log.info("Received request to delete all users");
        userService.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/sessions/{email}/all")
    public ResponseEntity<List<Session>> getAllSessionForUser(@PathVariable String email){
        log.info("Request to retrieve session for auth {}", email);
       List<Session> all =  sessionService.getSessionsForUser(email);
       log.info("Returning {} sessions for auth {}", all.size(), email);
        return ResponseEntity.ok().body(all);
    }

    @DeleteMapping("/sessions/{email}/all")
    public ResponseEntity<Void> deleteAllSessionForUser(@PathVariable String email){
        log.info("Request to retrieve session for auth {}", email);
        sessionService.deleteAllForUser(email);
        log.info("Deleted all sessions for auth {}",  email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/sessions/{email}/active")
    public ResponseEntity<Session> getActiveSessionForUser(@PathVariable String email){
        log.info("Request to retrieve session for auth {}", email);
        Session session =  sessionService.getActiveSessionForUser(email);
        log.info("Returning active session for auth {}", email);
        return ResponseEntity.ok().body(session);
    }
}

