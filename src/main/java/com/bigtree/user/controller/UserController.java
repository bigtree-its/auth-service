package com.bigtree.user.controller;

import com.bigtree.user.entity.Session;
import com.bigtree.user.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.bigtree.user.entity.User;
import com.bigtree.user.error.ApiException;
import com.bigtree.user.model.*;
import com.bigtree.user.service.LoginService;
import com.bigtree.user.service.UserService;
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
    public ResponseEntity<List<User>> getAll(){
        log.info("Received request to get all users");
        List<User> users = userService.getUsers();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<User> get(@PathVariable String userId){
        log.info("Received request to get user {}", userId);
        User user = userService.getUser(userId);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping(value = "/password-reset/initiate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> passwordResetInitiate(@Valid @RequestBody PasswordResetInitiate req){
        log.info("Received password reset initiate request for user {}", req.getEmail());
        loginService.passwordResetInitiate(req.getEmail());
        ApiResponse apiResponse = ApiResponse.builder().message("An OTP sent to your registered email address. Please use that to reset your password").build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PostMapping(value = "/password-reset/submit", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> passwordResetSubmit(@Valid @RequestBody PasswordResetSubmit req){
        log.info("Received password reset request for user {}", req.getEmail());
        loginService.passwordResetSubmit(req);
        ApiResponse apiResponse = ApiResponse.builder().message("Your password has been successfully changed.").build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        log.info("Received request to login for user {}", loginRequest.getEmail());
        LoginResponse response = loginService.login(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/logout", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request){
        log.info("Received request to logout user {}", request.getUserId());
        loginService.logout(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest){
        log.info("Received request to register new user {}", userRegistrationRequest);
        boolean success = userService.registerUser(userRegistrationRequest);
        if ( success){
            ApiResponse apiResponse = ApiResponse.builder().message("User registration successful. Please login with your credentials").build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "User registration failed");
    }


    @PutMapping(value = "/{userId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> update(@PathVariable String userId, @Valid @RequestBody User user){
        log.info("Received request to update user {}", userId);
        User updated = userService.updateUser(userId, user);
        return ResponseEntity.ok().body(updated);
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> delete(@PathVariable String userId){
        log.info("Received request to delete user {}", userId);
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
        log.info("Request to retrieve session for user {}", email);
       List<Session> all =  sessionService.getSessionsForUser(email);
       log.info("Returning {} sessions for user {}", all.size(), email);
        return ResponseEntity.ok().body(all);
    }

    @DeleteMapping("/sessions/{email}/all")
    public ResponseEntity<Void> deleteAllSessionForUser(@PathVariable String email){
        log.info("Request to retrieve session for user {}", email);
        sessionService.deleteAllForUser(email);
        log.info("Deleted all sessions for user {}",  email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/sessions/{email}/active")
    public ResponseEntity<Session> getActiveSessionForUser(@PathVariable String email){
        log.info("Request to retrieve session for user {}", email);
        Session session =  sessionService.getActiveSessionForUser(email);
        log.info("Returning active session for user {}", email);
        return ResponseEntity.ok().body(session);
    }
}

