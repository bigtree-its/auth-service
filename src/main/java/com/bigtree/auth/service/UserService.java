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
            if (StringUtils.isNotEmpty(user.getName())) {
                exist.setName(user.getName());
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
            Account byUserId = accountRepository.findByUserId(_id);
            if ( byUserId != null){
                accountRepository.delete(byUserId);
                log.info("Cleaned up accounts for user {}", _id);
            }
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
        if (req.getUserType() == UserType.Business && req.getBusinessType() == null) {
            log.error("Business Type is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "Business Type is mandatory");
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
                .name(req.getName())
                .mobile(req.getMobile())
                .userType(req.getUserType())
                .businessType(req.getBusinessType())
                .businessId(req.getBusinessId())
                .build();

        final User user = repository.save(newUser);
        if (user.get_id() != null) {
            log.info("New user created {} as {}", user.get_id(), user.getUserType().name());
            String activationCode = RandomStringUtils.random(6, "123456789abcdefghijklmno");
            Account account = accountRepository.save(Account.builder()
                    .userId(user.get_id())
                    .activationCode(activationCode)
                    .password(StringUtils.isEmpty(clientSecret) ? req.getPassword() : clientSecret)
                    .passwordChanged(LocalDateTime.now())
                    .build());
            if (account.get_id() != null) {
                log.info("Account created for user {} with id {}", user.getEmail(), user.get_id());
                if (req.getUserType() == UserType.CustomerApp || req.getUserType() == UserType.SupplierApp) {
                    Map<String, String> claims = new HashMap<>();
                    claims.put("client_id", user.getUserId());
                    claims.put("client_secret", account.getPassword());
                    claims.put("client_type", user.getUserType().getName());
                    claims.put("client_email", user.getEmail());
                    message = jwtService.createPrivateKeyJwt(claims, user);
                } else {
                    message = "Signup successful";
                }
                emailService.sendAccountActivationEmail(account,user);
                return ApiResponse.builder().endpoint("/register").message(message).build();
            }
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Could not complete your request. Please reach out to customer support");
    }

    private String generateUserId(UserType userType) {

        String userId = "";
        boolean unique = false;
        while (!unique) {
            userId = RandomStringUtils.random(6, "123456");
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
        List<User> all = repository.findAll();
        for (User user : all) {
            Account account = accountRepository.findByUserId(user.get_id());
            if ( account != null){
                accountRepository.delete(account);
            }
            repository.delete(user);
        }
    }

    public ApiResponse getPrivateKeyJwt(@Valid MultiValueMap<String, String> formParams) {
        Map<String, String> parameters = new HashMap<>();
        for (String theKey : formParams.keySet()) {
            parameters.put(theKey, formParams.getFirst(theKey));
        }
        AuthRequest authRequest = prepareRequest(parameters);
        if (StringUtils.isNotEmpty(authRequest.getClientEmail())) {
            User user = repository.findByEmail(authRequest.getClientEmail());
            if (user != null) {
                log.info("Found a user with email {}", authRequest.getClientEmail());
                Account account = accountRepository.findByUserId(user.get_id());
                if (account != null) {
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
        if (byId.isPresent()) {
            User user = byId.get();
            if (StringUtils.isEmpty(personalDetails.getName())) {
                user.setName(personalDetails.getName());
            }
            if (StringUtils.isEmpty(personalDetails.getMobile())) {
                user.setMobile(personalDetails.getMobile());
            }
            return repository.save(user);
        }
        log.info("Customer not found {}", personalDetails.getCustomerId());
        return null;
    }

    public void activateAccount(ActivateAccountRequest req) {
        log.info("Activating account {}", req.getAccountId());
        Optional<Account> accountOps = accountRepository.findById(req.getAccountId());
        if ( accountOps.isPresent()){
            Account account = accountOps.get();
            if (account.getActivationCode().equalsIgnoreCase(req.getActivationCode())){
                account.setActive(true);
                accountRepository.save(account);
                log.info("Account {} is activated", account.get_id());
            }else{
                throw new ApiException(HttpStatus.BAD_REQUEST, "Activation Code not found");
            }
        }else{
            throw new ApiException(HttpStatus.BAD_REQUEST, "Account not found");
        }
    }
}
