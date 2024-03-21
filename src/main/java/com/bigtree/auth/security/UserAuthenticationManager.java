package com.bigtree.auth.security;

import com.bigtree.auth.entity.Account;
import com.bigtree.auth.entity.User;
import com.bigtree.auth.repository.AccountRepository;
import com.bigtree.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserAuthenticationManager implements AuthenticationManager {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken request = (UsernamePasswordAuthenticationToken) authentication;
        log.info("Authenticating User {}", request.getPrincipal());
        User user = userRepository.findByEmail((String) request.getPrincipal());
        if ( user != null){
            log.info("User {} found as {}", user.getEmail(), user.getUserType().getName());
            Account account = accountRepository.findByUserIdAndPassword(user.get_id(), (String) request.getCredentials());
            if ( account != null){
                log.info("User authenticated");
                return authentication;
            }else{
                log.error("User not authenticated. Username and password are not matching");
            }
        }
        return null;
    }
}
