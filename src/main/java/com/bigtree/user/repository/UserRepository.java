package com.bigtree.user.repository;

import com.bigtree.user.entity.User;
import com.bigtree.user.entity.UserType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends MongoRepository<User, String> {

    User findByEmail(String email);
    User findByEmailAndUserType(String email, UserType userType);
}
