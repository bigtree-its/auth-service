package com.bigtree.user.service;


import com.bigtree.user.entity.User;
import com.bigtree.user.entity.UserAccount;
import com.bigtree.user.entity.UserType;
import com.bigtree.user.error.ApiException;
import com.bigtree.user.model.UserRegistrationRequest;
import com.bigtree.user.repository.UserAccountRepository;
import com.bigtree.user.repository.UserRepository;
import com.bigtree.user.security.CryptoHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    CryptoHelper cryptoHelper;

    public List<User> getUsers() {
        log.info("Fetching all users");
        return repository.findAll();
    }

    public User updateUser(String _id, User user) {
        Optional<User> optional = repository.findById(_id);
        if (optional.isPresent()) {
            log.info("User already exist. Updating");
            User exist = optional.get();
            if (StringUtils.hasLength(user.getFirstName())) {
                exist.setFirstName(user.getFirstName());
            }
            if (StringUtils.hasLength(user.getLastName())) {
                exist.setLastName(user.getLastName());
            }
            if (StringUtils.hasLength(user.getMobile())) {
                exist.setMobile(user.getMobile());
            }
            if (StringUtils.hasLength(user.getEmail())) {
                exist.setEmail(user.getEmail());
            }
            User updated = repository.save(exist);
            if (updated.get_id() != null) {
                log.info("User updated {}", updated.get_id());
            }
            return updated;
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User not exist");
        }

    }

    public void deleteUser(String _id) {
        Optional<User> optional = repository.findById(_id);
        if (optional.isPresent()) {
            log.info("User already exist. Deleting user");
            repository.deleteById(_id);
        } else {
            log.error("User not found");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User not found");
        }
    }

    public User getUser(String _id) {
        Optional<User> optional = repository.findById(_id);
        if (optional.isPresent()) {
            log.error("User found");
            return optional.get();
        } else {
            log.error("User not found");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User not found");
        }
    }


    public boolean registerUser(UserRegistrationRequest req) {

//        try {
//            final String decryptedFirstName = cryptoHelper.encryptAndEncode(req.getFirstName());
//            log.info("Decrypted : {}", decryptedFirstName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (req.getUserType() == null) {
            log.error("User type is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User type is mandatory");
        }
        if (!StringUtils.hasLength(req.getEmail())) {
            log.error("User email is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User email is mandatory");
        }
        if (!StringUtils.hasLength(req.getMobile())) {
            log.error("User mobile is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User mobile is mandatory");
        }
        if (!StringUtils.hasLength(req.getPassword())) {
            log.error("User password is mandatory");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User password is mandatory");
        }
        User user = repository.findByEmailAndUserType(req.getEmail(), req.getUserType());
        if (user != null && user.get_id() != null) {
            log.error("User already exist");
            throw new ApiException(HttpStatus.BAD_REQUEST, "User already exist");
        }
        log.info("Registering an user");
        User newUser = User.builder()
                .email(req.getEmail())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .mobile(req.getMobile())
                .userType(req.getUserType())
                .build();
        User saved = repository.save(newUser);
        if (saved.get_id() != null) {
            log.info("User created {}", saved.get_id());
            UserAccount account = userAccountRepository.save( UserAccount.builder()
                    .userId(saved.get_id())
                    .password(req.getPassword())
                    .passwordChanged(LocalDateTime.now())
                    .build());
            if (account.get_id() != null) {
                log.info("Account created");
                return true;
            }
        }
        return false;
    }

    public User findByEmailAndUserType(String email, UserType userType) {
        User byEmail = repository.findByEmailAndUserType(email, userType);
        if (byEmail == null) {
            log.info("User not found with email {}", email);
        }
        return byEmail;
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
