package com.bigtree.auth.repository;

import com.bigtree.auth.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

    Account findByIdentity(String identity);
    Account findByIdentityAndPassword(String identity, String password);
}
