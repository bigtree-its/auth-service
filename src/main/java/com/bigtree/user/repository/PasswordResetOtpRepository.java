package com.bigtree.user.repository;


import com.bigtree.user.entity.PasswordResetOtp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetOtpRepository extends MongoRepository<PasswordResetOtp, String> {

    @Query("SELECT a from PasswordResetOtp a where a.userId = :userId")
    PasswordResetOtp findByUserId(@Param("userId") String userId);
}