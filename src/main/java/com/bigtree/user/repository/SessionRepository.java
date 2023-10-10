package com.bigtree.user.repository;

import com.bigtree.user.entity.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends MongoRepository<Session, Session> {

    Session findByUserIdAndSessionId(String userId, String sessionId);

    List<Session> findByUserId(String userId);
}
