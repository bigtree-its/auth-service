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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
                final Session session = Session.builder().build();
                session.setStart(LocalDateTime.now());
                session.setUserId(user.get_id());
                session.setSessionId(RandomStringUtils.random(16, "abcdefghijklmnopqrstuvwxyz123456789"));
                Session sessionActive = sessionRepository.save(session);
                response = LoginResponse.builder()
                        .success(true)
                        .userId(user.get_id())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .sessionId(sessionActive.get_id())
                        .message("Login Success")
                        .build();
                log.info("Login successful for user {}", loginRequest.getEmail());
            } else {
                log.error("Cannot find account with provided username & password. {}", loginRequest.getEmail());
                response = LoginResponse.builder().success(false).message("Username or Password not match").build();
            }
        } else {
            response = LoginResponse.builder().success(false).message("User not found").build();
        }
        return response;
    }

    public void logout(LogoutRequest request) {
        Session session = sessionRepository.findByUserIdAndSessionId(request.getUserId(), request.getSessionId());
        if (session != null) {
            log.info("Session found. Logging out");
            session.setFinish(LocalDateTime.now());
            sessionRepository.save(session);
        }
    }

    public PasswordResetOtp passwordResetInitiate(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null || user.get_id() == null) {
            log.error("User not found");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User not found");
        }
        String salt = RandomStringUtils.random(6, "123456");
        PasswordResetOtp otp = PasswordResetOtp.builder()
                .otp(salt)
                .userId(user.get_id())
                .start(LocalDateTime.now())
                .build();

        PasswordResetOtp savedOtp = resetRepository.save(otp);
        log.info("Generated otp {}", savedOtp);
        return savedOtp;
    }

    public void passwordResetSubmit(PasswordResetSubmit req) {
        User user = userRepository.findByEmail(req.getEmail());
        if (user == null || user.get_id() == null) {
            log.error("User not found");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User not found");
        }
        PasswordResetOtp resetOtp = resetRepository.findByUserId(user.get_id());
        if (StringUtils.equals(resetOtp.getOtp(), req.getOtp())) {
            UserAccount account = userAccountRepository.getByUserId(user.get_id());
            account.setPassword(req.getPassword());
            account.setPasswordChanged(LocalDateTime.now());
            userAccountRepository.save(account);
            resetRepository.delete(resetOtp);
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "OTP not found or not matched");
        }
    }


}
