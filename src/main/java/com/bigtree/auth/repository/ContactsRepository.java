package com.bigtree.auth.repository;

import com.bigtree.auth.entity.Contacts;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactsRepository extends MongoRepository<Contacts, String> {

    Contacts findByEmail(String email);
    Contacts findByMobile(String mobile);
    Contacts findByResponded(boolean responded);
}