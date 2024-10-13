package com.bigtree.auth.service;

import com.bigtree.auth.entity.Account;
import com.bigtree.auth.entity.User;
import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.model.AuthRequest;
import com.bigtree.auth.model.GrantType;
import com.bigtree.auth.entity.UserType;
import com.bigtree.auth.model.TokenResponse;
import com.bigtree.auth.repository.AccountRepository;
import com.bigtree.auth.repository.UserRepository;
import com.bigtree.auth.security.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserAuthenticationService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    public TokenResponse authenticate(AuthRequest tokenRequest) {
        TokenResponse response;
        if (StringUtils.isEmpty(tokenRequest.getUsername()) || StringUtils.isEmpty(tokenRequest.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username and Password are mandatory for " + GrantType.PASSWORD.name() + " Grant Type");
        }
         if (tokenRequest.getUserType() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Token request is invalid");
        }
        log.info("Finding {} user with username {}", tokenRequest.getUserType(), tokenRequest.getUsername());
        final User user = userRepository.findByEmailAndUserType(tokenRequest.getUsername(), tokenRequest.getUserType());
        if (user != null) {
            log.info("Found a {} user with username {}",user.getUserType().getName(), tokenRequest.getUsername());
            Account account = accountRepository.findByUserId(user.get_id());
            if (account != null) {
                if ( passwordEncoder.matches(tokenRequest.getPassword(), account.getPassword())){
                    if (account.isActive()) {
                        String token = jwtTokenUtil.generateToken(user);
                        response = TokenResponse.builder().accessToken(token).build();
                        log.info("Authentication successful for user {}", tokenRequest.getUsername());
                    } else {
                        log.error("Authentication failed for user {}", tokenRequest.getUsername());
                        throw new ApiException(HttpStatus.UNAUTHORIZED, "Account not activated");
                    }
                }else{
                    log.error("Account password not matched for user {}", tokenRequest.getUsername());
                    throw new ApiException(HttpStatus.UNAUTHORIZED, "Account password not matched");
                }
            } else {
                log.error("Account not found for user {}", tokenRequest.getUsername());
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Cannot recognize the Username and Password");
            }
        } else {
            log.error("{} user not found with name {}",  tokenRequest.getUserType().getName(), tokenRequest.getUsername());
            throw new ApiException(HttpStatus.UNAUTHORIZED,  tokenRequest.getUserType().getName() + " user not found with name " + tokenRequest.getUsername());
        }
        return response;
    }
}
