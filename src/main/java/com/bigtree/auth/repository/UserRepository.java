package com.bigtree.auth.repository;

import com.bigtree.auth.entity.User;
import com.bigtree.auth.entity.UserType;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    List<User> findByNameLike(String name);
    User findByEmail(String email);
    User findByUserId(String userId);
    List<User> findByUserType(UserType userType);
    User findByUserIdAndUserType(String userId, UserType userType);
    User findByEmailAndUserType(String email, UserType userType);
}
