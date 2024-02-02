package com.bigtree.auth.controller;

import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.model.AuthRequest;
import com.bigtree.auth.model.TokenResponse;
import com.bigtree.auth.repository.IdentityRepository;
import com.bigtree.auth.security.JwtTokenUtil;
import com.bigtree.auth.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private IdentityRepository identityRepository;

    @RequestMapping(value = "/customer/token", method = RequestMethod.POST)
    public ResponseEntity<TokenResponse> customerToken(@RequestBody AuthRequest authenticationRequest) throws Exception {
        log.info("Request for customer token");
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final Identity userDetails = identityRepository.findByEmail(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(TokenResponse.builder().accessToken(token).build());
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new Exception("USER_DISABLED", e);
        }
    }
}