package com.bigtree.auth.repository;

import com.bigtree.auth.entity.PartnerSignup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerSignupRepository extends MongoRepository<PartnerSignup, String> {

    PartnerSignup findByEmail(String email);
    PartnerSignup findByMobile(String mobile);
}
