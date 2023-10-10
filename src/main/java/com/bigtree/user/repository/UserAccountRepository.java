package com.bigtree.user.repository;

import com.bigtree.user.entity.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends MongoRepository<UserAccount, String> {

    UserAccount getByUserId(String userId);
    UserAccount getByUserIdAndPassword(String userId, String password);
}
