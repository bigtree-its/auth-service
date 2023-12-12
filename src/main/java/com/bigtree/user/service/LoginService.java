package com.bigtree.user.service;

import com.bigtree.user.entity.PasswordResetOtp;
import com.bigtree.user.entity.Session;
import com.bigtree.user.entity.User;
import com.bigtree.user.entity.UserAccount;
import com.bigtree.user.error.ApiException;
import com.bigtree.user.model.*;
import com.bigtree.user.repository.PasswordResetOtpRepository;
import com.bigtree.user.repository.SessionRepository;
import com.bigtree.user.repository.UserAccountRepository;
import com.bigtree.user.repository.UserRepository;
import com.bigtree.user.security.JwtService;
import com.mongodb.client.result.DeleteResult;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class LoginService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    PasswordResetOtpRepository resetRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    EmailService emailService;

    @Autowired
    MongoTemplate mongoTemplate;

    public LoginResponse login(LoginRequest loginRequest) {

        LoginResponse response = null;
        if (StringUtils.isEmpty(loginRequest.getEmail())) {
            response = LoginResponse.builder().success(false).message("User email is mandatory").build();
        } else if (StringUtils.isEmpty(loginRequest.getPassword())) {
            response = LoginResponse.builder().success(false).message("User password is mandatory").build();
        } else if (Objects.isNull(loginRequest.getUserType())) {
            response = LoginResponse.builder().success(false).message("User type is mandatory").build();
        }
        if (response != null) {
            return response;
        }
        User user = userRepository.findByEmailAndUserType(loginRequest.getEmail(), loginRequest.getUserType());

        if (user != null) {
            log.info("Found an user {}", user.get_id());
            UserAccount account = userAccountRepository.getByUserIdAndPassword(user.get_id(), loginRequest.getPassword());
            if (account != null) {

                final String idToken = jwtService.generateIdToken(user);
                final String accessToken = jwtService.generateAccessToken(user.getEmail());
                final Session session = Session.builder().build();
                session.setStart(LocalDateTime.now());
                session.setUserId(user.get_id());
                session.setToken(idToken);
               sessionRepository.save(session);
                response = LoginResponse.builder()
                        .success(true)
                        .accessToken(accessToken)
                        .idToken(idToken)
                        .message("Login Success")
                        .build();
                log.info("Login successful for user {}", loginRequest.getEmail());
            } else {
                log.error("Login failed. Email and password combination not found {}", loginRequest.getEmail());
                throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot recognize the email and password");
            }
        } else {
            response = LoginResponse.builder().success(false).message("User not found").build();
        }
        return response;
    }

    public void logout(LogoutRequest request) {
        List<Session> sessions = sessionRepository.findByUserId(request.getUserId());
        if (!CollectionUtils.isEmpty(sessions)) {
            log.info("Session found. Logging out");
            Session session = sessions.get(0);
            session.setFinish(LocalDateTime.now());
            sessionRepository.save(session);
        }
    }

    public PasswordResetOtp passwordResetInitiate(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null || user.get_id() == null) {
            log.error("User not found");
            throw new ApiException(HttpStatus.BAD_REQUEST, "There was a problem. Cannot recognize the email.");
        }
        String salt = RandomStringUtils.random(6, "123456");
        PasswordResetOtp otp = PasswordResetOtp.builder()
                .otp(salt)
                .userId(user.get_id())
                .start(LocalDateTime.now())
                .build();

        PasswordResetOtp savedOtp = resetRepository.save(otp);
        log.info("Generated otp {}", savedOtp);
        emailService.setOnetimePasscode(email, user.getFullName(), savedOtp.getOtp());
        return savedOtp;
    }

    public void passwordResetSubmit(PasswordResetSubmit req) {
        try{
            User user = userRepository.findByEmail(req.getEmail());
            if (user == null || user.get_id() == null) {
                log.error("here was a problem. Cannot recognize the email. {}", req.getEmail());
                throw new ApiException(HttpStatus.BAD_REQUEST, "There was a problem. Cannot recognize the email.");
            }
            boolean changed = false;
            final List<PasswordResetOtp> list = resetRepository.findAllByUserId(user.get_id());
            if (! CollectionUtils.isEmpty(list)){
                for (PasswordResetOtp passwordResetOtp : list) {
                    if (StringUtils.equals(passwordResetOtp.getOtp(), req.getOtp())) {
                        UserAccount account = userAccountRepository.getByUserId(user.get_id());
                        account.setPassword(req.getPassword());
                        account.setPasswordChanged(LocalDateTime.now());
                        userAccountRepository.save(account);
                        Query query = new Query();
                        query.addCriteria(Criteria.where("userId").is(user.get_id()));
                        final DeleteResult deleteResult = mongoTemplate.remove(query, "resets");
                        log.info("Password reset successful for user {}. Removed old otp {}",user.getEmail(), deleteResult.getDeletedCount());
                        emailService.setPasswordResetConfirmation(user.getEmail(), user.getFullName());
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


}
