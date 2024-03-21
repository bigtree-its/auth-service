package com.bigtree.auth.controller;

import com.bigtree.auth.model.*;
import com.bigtree.auth.repository.UserRepository;
import com.bigtree.auth.security.JwtTokenUtil;
import com.bigtree.auth.service.UserAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public ResponseEntity<TokenResponse> token(@RequestBody AuthRequest authenticationRequest) throws Exception {
        log.info("Request for user token");
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        TokenResponse tokenResponse = userAuthenticationService.authenticate(authenticationRequest);
        return ResponseEntity.ok(tokenResponse);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new Exception("USER_DISABLED", e);
        }
    }

}