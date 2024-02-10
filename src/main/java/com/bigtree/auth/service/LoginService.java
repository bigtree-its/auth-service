package com.bigtree.auth.service;

import com.bigtree.auth.entity.*;
import com.bigtree.auth.error.ApiException;
import com.bigtree.auth.model.*;
import com.bigtree.auth.repository.PasswordResetOtpRepository;
import com.bigtree.auth.repository.SessionRepository;
import com.bigtree.auth.repository.AccountRepository;
import com.bigtree.auth.repository.IdentityRepository;
import com.bigtree.auth.security.CryptoHelper;
import com.bigtree.auth.security.JwtService;
import com.bigtree.auth.security.JwtTokenUtil;
import com.mongodb.client.result.DeleteResult;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class LoginService {

    @Autowired
    IdentityRepository identityRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    PasswordResetOtpRepository resetRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    EmailService emailService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    CryptoHelper cryptoHelper;

    public TokenResponse token(@Valid MultiValueMap<String, String> formParams) {

        TokenResponse response = null;

        Map<String,String> parameters = new HashMap<>();
        for (String theKey : formParams.keySet()) {
            parameters.put(theKey, formParams.getFirst(theKey));
        }

        final TokenRequest tokenRequest = prepareRequest(parameters);
        if (StringUtils.isEmpty(tokenRequest.getGrantType())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "grant_type is mandatory");
        }
        if (Objects.isNull(tokenRequest.getClientType())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "client_type is mandatory");
        }
        if ( !StringUtils.equalsIgnoreCase(tokenRequest.getGrantType(), GrantType.CLIENT_CREDENTIALS.name()) && !StringUtils.equalsIgnoreCase(tokenRequest.getGrantType(), GrantType.PASSWORD.name())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid Grant Type");
        }
        // Grant Type Client Credentials
        if (StringUtils.equalsIgnoreCase(tokenRequest.getGrantType(), GrantType.CLIENT_CREDENTIALS.name()) ) {
            validateClientCredentials(tokenRequest);
            if ( StringUtils.isNotEmpty(tokenRequest.getClientId()) && StringUtils.isNotEmpty(tokenRequest.getClientSecret())){
                response = authTreeClientIdAndSecret(tokenRequest);
            }else  if ( StringUtils.isNotEmpty(tokenRequest.getClientAssertion())){
                response = authTreeClientAssertion(tokenRequest);
            }
        }

        return response;
    }

    private TokenResponse authTreePassword(TokenRequest tokenRequest) {
        Identity identity;
        TokenResponse response;
        if ( StringUtils.isEmpty(tokenRequest.getUsername()) || StringUtils.isEmpty(tokenRequest.getPassword())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username and Password are mandatory for "+GrantType.PASSWORD.name()+ " Grant Type");
        }
        identity = identityRepository.findByEmailAndClientType(tokenRequest.getUsername(), tokenRequest.getClientType());
        if ( identity != null) {
            log.info("Found an identity {}", tokenRequest.getUsername());
            Account account = accountRepository.findByIdentityAndPassword(identity.get_id(), tokenRequest.getPassword());
            if (account != null) {
                response = generateToken(identity);
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

    private TokenResponse authTreeClientAssertion(TokenRequest tokenRequest) {
        log.info("Authenticating machine user");
        Identity identity;
        TokenResponse response;
        Boolean tokenExpired = jwtTokenUtil.isTokenExpired(tokenRequest.getClientAssertion());
        if (tokenExpired == null || tokenExpired){
            log.error("Authentication failed for client {}",  tokenRequest.getClientId());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Cannot recognize the client "+ tokenRequest.getClientId());
        }
        Claims claims = jwtTokenUtil.getAllClaimsFromToken(tokenRequest.getClientAssertion());
        final String clientId = claims.get("client_id", String.class);
        final String clientSecret = claims.get("client_secret", String.class);
        final String clientType = claims.get("client_type", String.class);
        final String clientEmail = claims.get("client_email", String.class);
        identity = identityRepository.findByClientIdAndClientType(clientId, ClientType.fromName(clientType));
        if ( identity != null) {
            Account account = accountRepository.findByIdentityAndPassword(identity.get_id(), clientSecret);
            if (account != null) {
                response = generateToken(identity);
                log.info("Authentication successful for client {}", identity.getClientId());
            } else {
                log.error("Authentication failed for client {}",  identity.getClientId());
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Cannot recognize the Username and Password");
            }
        }else{
            log.error("Authentication failed for client {}",  tokenRequest.getClientId());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Cannot recognize the client "+ tokenRequest.getClientId());
        }
        return response;
    }

    private TokenResponse authTreeClientIdAndSecret(TokenRequest tokenRequest) {
        TokenResponse response = null;
        Identity identity = identityRepository.findByClientIdAndClientType(tokenRequest.getClientId(), tokenRequest.getClientType());
        if ( identity != null) {
            Account account = accountRepository.findByIdentityAndPassword(identity.get_id(), tokenRequest.getClientSecret());
            if (account != null) {
                response = generateToken(identity);
                log.info("Authentication successful for client {}", identity.getClientId());
            } else {
                log.error("Authentication failed for client {}",  identity.getClientId());
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Cannot recognize the email and password");
            }
        }else{
            log.error("Authentication failed for client {}",  tokenRequest.getClientId());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Cannot recognize the client "+ tokenRequest.getClientId());
        }
        return response;
    }

    private TokenRequest prepareRequest(Map<String, String> parameters) {
        final TokenRequest tokenRequest = TokenRequest.builder().build();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if ( key.equalsIgnoreCase("grant_type")){
                tokenRequest.setGrantType(value);
            }else if ( key.equalsIgnoreCase("username")){
                tokenRequest.setUsername(value);
            }else if ( key.equalsIgnoreCase("client_id")){
                tokenRequest.setClientId(value);
            }else if ( key.equalsIgnoreCase("client_secret")){
                tokenRequest.setClientSecret(value);
            }else if ( key.equalsIgnoreCase("client_assertion")){
                tokenRequest.setClientAssertion(value);
            }else if ( key.equalsIgnoreCase("client_assertion_type")){
                tokenRequest.setClientAssertionType(value);
            }else if ( key.equalsIgnoreCase("password")){
                tokenRequest.setPassword(value);
            }else if ( key.equalsIgnoreCase("client_type")){
                tokenRequest.setClientType(ClientType.fromName(value));
            }
        }
        return tokenRequest;
    }

    private TokenResponse generateToken(Identity identity) {
        TokenResponse response;
        final String accessToken = jwtTokenUtil.generateToken(identity);
        final Session session = Session.builder().build();
        session.setStart(LocalDateTime.now());
        session.setUserId(identity.get_id());
        session.setToken(accessToken);
        sessionRepository.save(session);
        response = TokenResponse.builder()
                .accessToken(accessToken)
                .build();
        return response;
    }

    private void validateClientCredentials(TokenRequest tokenRequest) {
        if ( StringUtils.isEmpty(tokenRequest.getClientAssertion()) && StringUtils.isEmpty(tokenRequest.getClientId())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Either Client ID or Client Assertion is mandatory for "+GrantType.CLIENT_CREDENTIALS.getName());
        }
        if ( !StringUtils.isEmpty(tokenRequest.getClientId()) && (StringUtils.isEmpty(tokenRequest.getClientSecret()) && StringUtils.isEmpty(tokenRequest.getClientAssertion()) )){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Either Client Secret or Client Assertion is mandatory for "+GrantType.CLIENT_CREDENTIALS.getName());
        }
        if ( !StringUtils.isEmpty(tokenRequest.getClientAssertion()) && StringUtils.isEmpty(tokenRequest.getClientAssertionType())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Client Assertion Type is mandatory for "+GrantType.CLIENT_CREDENTIALS.getName());
        }
        if ( !StringUtils.isEmpty(tokenRequest.getClientAssertionType()) && StringUtils.isEmpty(tokenRequest.getClientAssertion())  ){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Client Assertion is mandatory for "+GrantType.CLIENT_CREDENTIALS.getName());
        }
    }

    public void logout(@Valid MultiValueMap request) {
        List<Session> sessions = Collections.emptyList();//sessionRepository.findByUserId(request.getUserId());
        if (!CollectionUtils.isEmpty(sessions)) {
            log.info("Session found. Logging out");
            Session session = sessions.get(0);
            session.setFinish(LocalDateTime.now());
            sessionRepository.save(session);
        }
    }

    public PasswordResetOtp passwordResetInitiate(String email) {
        Identity identity = identityRepository.findByEmail(email);
        if (identity == null || identity.get_id() == null) {
            log.error("Identity not found {}", email);
            throw new ApiException(HttpStatus.BAD_REQUEST, "There was a problem. Cannot recognize the email.");
        }
        String salt = RandomStringUtils.random(6, "123456");
        PasswordResetOtp otp = PasswordResetOtp.builder()
                .otp(salt)
                .userId(identity.get_id())
                .start(LocalDateTime.now())
                .build();

        PasswordResetOtp savedOtp = resetRepository.save(otp);
        log.info("Generated otp {}", savedOtp);
        emailService.setOnetimePasscode(email, identity.getFullName(), savedOtp.getOtp());
        return savedOtp;
    }

    public void passwordResetSubmit(PasswordResetSubmit req) {
        try{
            Identity identity = identityRepository.findByEmail(req.getEmail());
            if (identity == null || identity.get_id() == null) {
                log.error("here was a problem. Cannot recognize the email. {}", req.getEmail());
                throw new ApiException(HttpStatus.BAD_REQUEST, "There was a problem. Cannot recognize the email.");
            }
            boolean changed = false;
            final List<PasswordResetOtp> list = resetRepository.findAllByUserId(identity.get_id());
            if (! CollectionUtils.isEmpty(list)){
                for (PasswordResetOtp passwordResetOtp : list) {
                    if (StringUtils.equals(passwordResetOtp.getOtp(), req.getOtp())) {
                        Account account = accountRepository.findByIdentity(identity.get_id());
                        account.setPassword(req.getPassword());
                        account.setPasswordChanged(LocalDateTime.now());
                        accountRepository.save(account);
                        Query query = new Query();
                        query.addCriteria(Criteria.where("userId").is(identity.get_id()));
                        final DeleteResult deleteResult = mongoTemplate.remove(query, "resets");
                        log.info("Password reset successful for customer {}. Removed old otp {}", identity.getEmail(), deleteResult.getDeletedCount());
                        emailService.setPasswordResetConfirmation(identity.getEmail(), identity.getFullName());
                        changed = true;
                        break;
                    }
                }
            }
            if (! changed){
                throw new ApiException(HttpStatus.BAD_REQUEST, "There was a problem. OTP not found or not matched. Try to reset again");
            }
        }catch (Exception e){
            throw new ApiException(HttpStatus.BAD_REQUEST, "There was a problem. Please contact customer support to reset your password.");
        }
    }

    public Identity updatePassword(MultiValueMap form) {
        return null;
    }
}
