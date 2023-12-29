package com.bigtree.auth.service;

import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.entity.Session;
import com.bigtree.auth.repository.SessionRepository;
import com.bigtree.auth.repository.IdentityRepository;
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
    IdentityRepository identityRepository;

    public List<Session> getSessionsForUser(String clientId){
        Identity identity = identityRepository.findByClientId(clientId);
        if ( identity != null){
            List<Session> sessions = sessionRepository.findByUserId(identity.get_id());
            if (!CollectionUtils.isEmpty(sessions)){
                log.info("{} sessions found for client {}", sessions.size(), identity.getEmail());
            }else{
                log.info("No sessions found for client {}", identity.getEmail());
            }
            return sessions;
        }
        log.error("Identity not found with client {}", clientId);
        return Collections.emptyList();
    }

    public Session getActiveSessionForUser(String clientId){
        Session session = null;
        Identity identity = identityRepository.findByClientId(clientId);
        if ( identity != null){
            List<Session> sessions = sessionRepository.findByUserId(identity.get_id());
            if (!CollectionUtils.isEmpty(sessions)){
                log.info("{} sessions found for client {}", sessions.size(), identity.getEmail());
                for (Session s : sessions) {
                    if ( s.getFinish() == null){
                        session = s;
                        break;
                    }
                }
            }else{
                log.info("No sessions found for client {}", identity.getEmail());
            }
        }else{
            log.error("Identity not found with client {}", clientId);
        }

        return session;
    }

    public boolean deleteAllForUser(String clientId) {
        Identity identity = identityRepository.findByClientId(clientId);
        if (identity == null){
            log.error("Identity not found with client {}", clientId);
            return false;
        }
        List<Session> sessions = sessionRepository.findByUserId(identity.get_id());
        sessionRepository.deleteAll(sessions);
        return true;
    }
}
