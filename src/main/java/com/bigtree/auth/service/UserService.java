package com.bigtree.auth.service;


import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.entity.Account;
import com.bigtree.auth.entity.ClientType;
import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.model.ApiResponse;
import com.bigtree.auth.model.UserRegistrationRequest;
import com.bigtree.auth.repository.AccountRepository;
import com.bigtree.auth.repository.IdentityRepository;
import com.bigtree.auth.security.CryptoHelper;
import com.bigtree.auth.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    IdentityRepository repository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CryptoHelper cryptoHelper;

    @Autowired
    JwtService jwtService;

    public List<Identity> getUsers() {
        log.info("Fetching all users");
        return repository.findAll();
    }

    public Identity updateUser(String _id, Identity identity) {
        Optional<Identity> optional = repository.findById(_id);
        if (optional.isPresent()) {
            log.info("Identity already exist. Updating");
            Identity exist = optional.get();
            if (StringUtils.hasLength(identity.getFirstName())) {
                exist.setFirstName(identity.getFirstName());
            }
            if (StringUtils.hasLength(identity.getLastName())) {
                exist.setLastName(identity.getLastName());
            }
            if (StringUtils.hasLength(identity.getMobile())) {
                exist.setMobile(identity.getMobile());
            }
            if (StringUtils.hasLength(identity.getEmail())) {
                exist.setEmail(identity.getEmail());
            }
            Identity updated = repository.save(exist);
            if (updated.get_id() != null) {
                log.info("Identity updated {}", updated.get_id());
            }
            return updated;
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Identity not exist");
        }

    }

    public void deleteUser(String _id) {
        Optional<Identity> optional = repository.findById(_id);
        if (optional.isPresent()) {
            log.info("Identity already exist. Deleting auth");
            repository.deleteById(_id);
        } else {
            log.error("Identity not found");
            throw new ApiException(HttpStatus.BAD_REQUEST, "Identity not found");
        }
    }

    public Identity getUser(String _id) {
        Optional<Identity> optional = repository.findById(_id);
        if (optional.isPresent()) {
            log.error("Identity found");
            return optional.get();
        } else {
            log.error("Identity not found");
            throw new ApiException(HttpStatus.BAD_REQUEST, "Identity not found");
        }
    }


    public ApiResponse registerUser(UserRegistrationRequest req) {

        if (req.getClientType() == null) {
            log.error("Identity type is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "Client type is mandatory");
        }
        if (!StringUtils.hasLength(req.getEmail())) {
            log.error("Identity email is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "Client email is mandatory");
        }
        if (!StringUtils.hasLength(req.getMobile())) {
            log.error("Identity mobile is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "Client mobile is mandatory");
        }
        if (req.getClientType() != ClientType.CUSTOMER_APP && req.getClientType() != ClientType.SUPPLIER_APP && !StringUtils.hasLength(req.getPassword())) {
            log.error("Identity password is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "Client password is mandatory");
        }
        Identity existing = repository.findByEmail(req.getEmail());
        if (existing != null && existing.get_id() != null && existing.getClientType().getName() == req.getClientType().getName()) {
            log.error("Identity already exist");
            throw new ApiException(HttpStatus.BAD_REQUEST, "Identity already exist");
        }
        String clientId = generateClientId(req.getClientType());
        String clientSecret = "";
        if ( req.getClientType() == ClientType.CUSTOMER_APP || req.getClientType() == ClientType.SUPPLIER_APP){
            clientSecret = RandomStringUtils.random(12, "123456789abcdefghijklmno");
        }
        Identity newIdentity = Identity.builder()
                .email(req.getEmail())
                .clientId(clientId)
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .mobile(req.getMobile())
                .clientType(req.getClientType())
                .build();

        final Identity identity = repository.save(newIdentity);
        if (identity.get_id() != null) {
            log.info("New identity created {}", identity.get_id());
            Account account = accountRepository.save( Account.builder()
                    .identity(identity.get_id())
                    .password(clientSecret != null? clientSecret : req.getPassword())
                    .passwordChanged(LocalDateTime.now())
                    .build());
            if (account.get_id() != null) {
                log.info("Account created");
                if ( req.getClientType() == ClientType.CUSTOMER_APP || req.getClientType() == ClientType.SUPPLIER_APP){
                    Map<String,String> claims = new HashMap<>();
                    claims.put("client_id", identity.getClientId());
                    claims.put("client_secret", account.getPassword());
                    claims.put("client_type", identity.getClientType().getName());
                    claims.put("client_email", identity.getEmail());
                    final String privateKeyJwt = jwtService.createPrivateKeyJwt(claims, identity);
                    return ApiResponse.builder().endpoint("/register").message(privateKeyJwt).build();
                }
                return ApiResponse.builder().endpoint("/register").message("Created").build();
            }
        }
        return ApiResponse.builder().endpoint("/register").message("").build();
    }

    private String generateClientId(ClientType clientType) {

        String prefix = "hoc-"+clientType.getCode()+"-";
        String clientId = "";
        boolean unique = false;
        while(!unique){
            clientId = prefix+ RandomStringUtils.random(6, "123456");
            final Identity exist = repository.findByClientId(clientId);
            unique = exist == null;
        }
        return clientId;
    }

    public Identity findByEmailAndUserType(String email, ClientType clientType) {
        Identity byEmail = repository.findByEmail(email);
        if (byEmail == null) {
            log.info("Identity not found with email {}", email);
        }
        return byEmail;
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
