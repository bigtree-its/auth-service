package com.bigtree.user.service;

import com.bigtree.user.entity.Session;
import com.bigtree.user.entity.User;
import com.bigtree.user.repository.SessionRepository;
import com.bigtree.user.repository.UserRepository;
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

    public List<Session> getSessionsForUser(String email){
        User user = userRepository.findByEmail(email);
        if ( user != null){
            List<Session> sessions = sessionRepository.findByUserId(user.get_id());
            if (!CollectionUtils.isEmpty(sessions)){
                log.info("{} sessions found for user {}", sessions.size(), user.getEmail());
            }else{
                log.info("No sessions found for user {}", user.getEmail());
            }
            return sessions;
        }
        log.error("User not found with user {}", email);
        return Collections.emptyList();
    }

    public Session getActiveSessionForUser(String email){
        Session session = null;
        User user = userRepository.findByEmail(email);
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
            log.error("User not found with user {}", email);
        }

        return session;
    }

    public boolean deleteAllForUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null){
            log.error("User not found with email {}", email);
            return false;
        }
        List<Session> sessions = sessionRepository.findByUserId(user.get_id());
        sessionRepository.deleteAll(sessions);
        return true;
    }
}
