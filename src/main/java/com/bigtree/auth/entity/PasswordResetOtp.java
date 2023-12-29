package com.bigtree.auth.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Document(collection = "resets")
@Builder
public class PasswordResetOtp {
    @MongoId
    private String _id;
    private String userId;
    private String otp;
    private LocalDateTime start;
}
