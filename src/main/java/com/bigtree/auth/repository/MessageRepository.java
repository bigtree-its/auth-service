package com.bigtree.auth.repository;

import com.bigtree.auth.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    Message findByEmail(String email);
    Message findByMobile(String mobile);
    Message findByResponded(boolean responded);
}