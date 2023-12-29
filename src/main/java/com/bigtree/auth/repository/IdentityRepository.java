package com.bigtree.auth.repository;

import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.entity.ClientType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityRepository extends MongoRepository<Identity, String> {

    Identity findByEmail(String email);
    Identity findByClientId(String clientId);
    Identity findByClientIdAndClientType(String clientId, ClientType clientType);
    Identity findByEmailAndClientType(String email, ClientType clientType);
}
