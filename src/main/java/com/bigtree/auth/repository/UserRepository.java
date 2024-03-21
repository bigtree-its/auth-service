package com.bigtree.auth.repository;

import com.bigtree.auth.entity.User;
import com.bigtree.auth.entity.UserType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);
    User findByUserId(String userId);
    User findByUserIdAndUserType(String userId, UserType userType);
    User findByEmailAndUserType(String email, UserType userType);
}
