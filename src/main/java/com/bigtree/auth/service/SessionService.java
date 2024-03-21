package com.bigtree.auth.service;

import com.bigtree.auth.entity.User;
import com.bigtree.auth.entity.Session;
import com.bigtree.auth.repository.SessionRepository;
import com.bigtree.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SessionService {

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    UserRepository userRepository;

    public List<Session> getSessionsForUser(String userId){
        User user = userRepository.findByUserId(userId);
        if ( user != null){
            List<Session> sessions = sessionRepository.findByUserId(user.get_id());
            if (!CollectionUtils.isEmpty(sessions)){
                log.info("{} sessions found for user {}", sessions.size(), user.getEmail());
            }else{
                log.info("No sessions found for user {}", user.getEmail());
            }
            return sessions;
        }
        log.error("User not found with id {}", userId);
        return Collections.emptyList();
    }

    public Session getActiveSessionForUser(String userId){
        Session session = null;
        User user = userRepository.findByUserId(userId);
        if ( user != null){
            List<Session> sessions = sessionRepository.findByUserId(user.get_id());
            if (!CollectionUtils.isEmpty(sessions)){
                log.info("{} sessions found for user {}", sessions.size(), user.getEmail());
                for (Session s : sessions) {
                    if ( s.getFinish() == null){
                        session = s;
                        break;
                    }
                }
            }else{
                log.info("No sessions found for user {}", user.getEmail());
            }
        }else{
            log.error("User not found with userId {}", userId);
        }

        return session;
    }

    public boolean deleteAllForUser(String userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null){
            log.error("User not found with id {}", userId);
            return false;
        }
        List<Session> sessions = sessionRepository.findByUserId(user.get_id());
        sessionRepository.deleteAll(sessions);
        return true;
    }
}
