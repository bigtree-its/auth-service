package com.bigtree.auth.service;

import com.bigtree.auth.entity.Account;
import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.repository.AccountRepository;
import com.bigtree.auth.repository.IdentityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RetailCustomerAuthenticator implements AuthenticationManager {

    @Autowired
    IdentityRepository identityRepository;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken request = (UsernamePasswordAuthenticationToken) authentication;
       log.info("Authenticating retail customer {}", request.getPrincipal());
        Identity customer = identityRepository.findByEmail((String) request.getPrincipal());
        if ( customer != null){
            log.info("Customer found.. {}", customer);
            Account account = accountRepository.findByIdentityAndPassword(customer.get_id(), (String) request.getCredentials());
            if ( account != null){
                log.info("Customer authenticated");
            }else{
                log.error("Customer not authenticated");
            }
        }
        return null;
    }
}
