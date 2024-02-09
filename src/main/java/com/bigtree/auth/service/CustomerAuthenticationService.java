package com.bigtree.auth.service;

import com.bigtree.auth.entity.Account;
import com.bigtree.auth.entity.ClientType;
import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.model.AuthRequest;
import com.bigtree.auth.model.GrantType;
import com.bigtree.auth.model.TokenRequest;
import com.bigtree.auth.model.TokenResponse;
import com.bigtree.auth.repository.AccountRepository;
import com.bigtree.auth.repository.IdentityRepository;
import com.bigtree.auth.security.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerAuthenticationService {

    @Autowired
    IdentityRepository identityRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public TokenResponse authenticate(AuthRequest tokenRequest){
        Identity identity;
        TokenResponse response;
        if ( StringUtils.isEmpty(tokenRequest.getUsername()) || StringUtils.isEmpty(tokenRequest.getPassword())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username and Password are mandatory for "+ GrantType.PASSWORD.name()+ " Grant Type");
        }
        identity = identityRepository.findByEmailAndClientType(tokenRequest.getUsername(), ClientType.Customer);
        if ( identity != null) {
            log.info("Found an identity {}", tokenRequest.getUsername());
            Account account = accountRepository.findByIdentityAndPassword(identity.get_id(), tokenRequest.getPassword());
            if (account != null) {
                String token = jwtTokenUtil.generateToken(identity);
                response = TokenResponse.builder().accessToken(token).build();
                log.info("Authentication successful for client {}", tokenRequest.getUsername());
            } else {
                log.error("Authentication failed for client {}",  tokenRequest.getUsername());
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Cannot recognize the Username and Password");
            }
        }else{
            log.error("Authentication failed for client {}",  tokenRequest.getUsername());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Cannot recognize the client "+ tokenRequest.getUsername());
        }
        return response;
    }
}
