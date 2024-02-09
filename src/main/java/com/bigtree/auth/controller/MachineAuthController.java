package com.bigtree.auth.controller;

import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.model.TokenResponse;
import com.bigtree.auth.security.JwtTokenUtil;
import com.bigtree.auth.service.LoginService;
import io.jsonwebtoken.Claims;
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
@RequestMapping("/authenticate/machine")
@CrossOrigin(origins = "*")
public class MachineAuthController {

    @Autowired
    LoginService loginService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;


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

    @PostMapping(value = "/verify")
    public ResponseEntity<?> verify(@RequestHeader("Authorization") String authorization){
        log.info("Received request to verify token {}", authorization);
        String jwtToken = authorization.substring(7);
        Claims claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
        Boolean tokenExpired = jwtTokenUtil.isTokenExpired(jwtToken);
        log.info("Claims {}", claims.toString());
        return ResponseEntity.status(tokenExpired?HttpStatus.OK: HttpStatus.UNAUTHORIZED).body(tokenExpired? "OK":"Unauthorized");
    }

}
