package com.bigtree.auth.service;


import com.bigtree.auth.entity.User;
import com.bigtree.auth.entity.Account;
import com.bigtree.auth.entity.UserType;
import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.model.*;
import com.bigtree.auth.repository.AccountRepository;
import com.bigtree.auth.repository.UserRepository;
import com.bigtree.auth.security.JwtTokenUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    JwtTokenUtil jwtService;

    @Autowired
    EmailService emailService;

    public List<User> getUsers() {
        log.info("Fetching all users");
        return repository.findAll();
    }

    public User updateUser(String _id, User user) {
        Optional<User> optional = repository.findById(_id);
        if (optional.isPresent()) {
            log.info("Identity already exist. Updating");
            User exist = optional.get();
            if (StringUtils.isNotEmpty(user.getFirstName())) {
                exist.setFirstName(user.getFirstName());
            }
            if (StringUtils.isNotEmpty(user.getLastName())) {
                exist.setLastName(user.getLastName());
            }
            if (StringUtils.isNotEmpty(user.getMobile())) {
                exist.setMobile(user.getMobile());
            }
            if (StringUtils.isNotEmpty(user.getEmail())) {
                exist.setEmail(user.getEmail());
            }
            User updated = repository.save(exist);
            if (updated.get_id() != null) {
                log.info("Identity updated {}", updated.get_id());
            }
            return updated;
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Identity not exist");
        }

    }

    public void deleteUser(String _id) {
        Optional<User> optional = repository.findById(_id);
        if (optional.isPresent()) {
            log.info("Identity already exist. Deleting identity");
            repository.deleteById(_id);
        } else {
            log.error("Identity not found");
            throw new ApiException(HttpStatus.BAD_REQUEST, "Identity not found");
        }
    }

    public User getUser(String _id) {
        Optional<User> optional = repository.findById(_id);
        if (optional.isPresent()) {
            log.error("Identity found");
            return optional.get();
        } else {
            log.error("Identity not found");
            throw new ApiException(HttpStatus.BAD_REQUEST, "Identity not found");
        }
    }


    public ApiResponse registerUser(UserRegistrationRequest req) {

        if (req.getUserType() == null) {
            log.error("User type is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User type is mandatory");
        }
        if (StringUtils.isEmpty(req.getEmail())) {
            log.error("User email is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User email is mandatory");
        }
        if (StringUtils.isEmpty(req.getMobile())) {
            log.error("User mobile is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User mobile is mandatory");
        }
        if (req.getUserType() != UserType.CustomerApp && req.getUserType() != UserType.SupplierApp && StringUtils.isEmpty(req.getPassword())) {
            log.error("User password is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User password is mandatory");
        }
        User existing = repository.findByEmail(req.getEmail());
        if (existing != null) {
            log.error("User already exist {}", existing.getEmail());
            throw new ApiException(HttpStatus.BAD_REQUEST, "User already exist");
        }
        String message = "";
        String userId = generateUserId(req.getUserType());
        String clientSecret = "";
        if (req.getUserType() == UserType.CustomerApp || req.getUserType() == UserType.SupplierApp) {
            clientSecret = RandomStringUtils.random(12, "123456789abcdefghijklmno");
        }
        User newUser = User.builder()
                .email(req.getEmail())
                .userId(userId)
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .mobile(req.getMobile())
                .userType(req.getUserType())
                .build();

        final User user = repository.save(newUser);
        if (user.get_id() != null) {
            log.info("New user created {} as {}", user.get_id(), user.getUserType().name());
            Account account = accountRepository.save(Account.builder()
                    .userId(user.get_id())
                    .password(StringUtils.isEmpty(clientSecret)?  req.getPassword(): clientSecret)
                    .passwordChanged(LocalDateTime.now())
                    .build());
            if (account.get_id() != null) {
                log.info("Account secrets created for user {} with id {}", user.getEmail(), user.get_id());
                if (req.getUserType() == UserType.CustomerApp || req.getUserType() == UserType.SupplierApp) {
                    Map<String, String> claims = new HashMap<>();
                    claims.put("client_id", user.getUserId());
                    claims.put("client_secret", account.getPassword());
                    claims.put("client_type", user.getUserType().getName());
                    claims.put("client_email", user.getEmail());
                    message = jwtService.createPrivateKeyJwt(claims, user);
                }else{
                    message = "Signup successful";
                }
                emailService.senSignupCompletion(user);
                return ApiResponse.builder().endpoint("/register").message(message).build();
            }
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Could not complete your request. Please reach out to customer support");
    }

    private String generateUserId(UserType userType) {

        String prefix = "desi-" + userType.getCode() + "-";
        String userId = "";
        boolean unique = false;
        while (!unique) {
            userId = prefix + RandomStringUtils.random(6, "123456");
            final User exist = repository.findByUserId(userId);
            unique = exist == null;
        }
        return userId;
    }

    public User findByEmailAndUserType(String email, UserType userType) {
        User byEmail = repository.findByEmail(email);
        if (byEmail == null) {
            log.info("Identity not found with email {}", email);
        }
        return byEmail;
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public ApiResponse getPrivateKeyJwt(@Valid MultiValueMap<String, String> formParams) {
        Map<String,String> parameters = new HashMap<>();
        for (String theKey : formParams.keySet()) {
            parameters.put(theKey, formParams.getFirst(theKey));
        }
        AuthRequest authRequest = prepareRequest(parameters);
        if ( StringUtils.isNotEmpty(authRequest.getClientEmail())){
            User user = repository.findByEmail(authRequest.getClientEmail());
            if ( user != null){
                log.info("Found a user with email {}", authRequest.getClientEmail());
                Account account = accountRepository.findByUserId(user.get_id());
                if ( account != null){
                    Map<String, String> claims = new HashMap<>();
                    claims.put("user_id", user.getUserId());
                    claims.put("user_secret", account.getPassword());
                    claims.put("user_type", user.getUserType().getName());
                    claims.put("user_email", user.getEmail());
                    final String privateKeyJwt = jwtService.createPrivateKeyJwt(claims, user);
                    return ApiResponse.builder().endpoint("/private-key-jwt").message(privateKeyJwt).build();
                }
            }
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Identity details required");
    }

    private AuthRequest prepareRequest(Map<String, String> parameters) {
        final AuthRequest authRequest = AuthRequest.builder().build();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (key.equalsIgnoreCase("grant_type")) {
                authRequest.setGrantType(value);
            } else if (key.equalsIgnoreCase("username")) {
                authRequest.setUsername(value);
            } else if (key.equalsIgnoreCase("client_id")) {
                authRequest.setClientId(value);
            } else if (key.equalsIgnoreCase("client_secret")) {
                authRequest.setClientSecret(value);
            } else if (key.equalsIgnoreCase("client_assertion")) {
                authRequest.setClientAssertion(value);
            } else if (key.equalsIgnoreCase("client_assertion_type")) {
                authRequest.setClientAssertionType(value);
            } else if (key.equalsIgnoreCase("password")) {
                authRequest.setPassword(value);
            } else if (key.equalsIgnoreCase("client_type")) {
                authRequest.setUserType(UserType.fromName(value));
            } else if (key.equalsIgnoreCase("client_email")) {
                authRequest.setClientEmail(value);
            }
        }
        log.info("The Auth Request {}", authRequest.toString());
        return authRequest;
    }

    public User updatePersonal(PersonalDetails personalDetails) {
        Optional<User> byId = repository.findById(personalDetails.getCustomerId());
        if ( byId.isPresent()){
            User user = byId.get();
            if ( StringUtils.isEmpty(personalDetails.getFirstName())){
                user.setFirstName(personalDetails.getFirstName());
            }
            if ( StringUtils.isEmpty(personalDetails.getLastName())){
                user.setLastName(personalDetails.getLastName());
            }
            if ( StringUtils.isEmpty(personalDetails.getMobile())){
                user.setMobile(personalDetails.getMobile());
            }
            return repository.save(user);
        }
        log.info("Customer not found {}", personalDetails.getCustomerId());
        return null;
    }
}
