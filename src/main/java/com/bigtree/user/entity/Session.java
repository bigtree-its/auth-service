package com.bigtree.user.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Document(collection = "sessions")
@Builder
public class Session {
    @MongoId
    private String _id;
    private String userId;
    private String token;
    private LocalDateTime start;
    private LocalDateTime finish;
}
