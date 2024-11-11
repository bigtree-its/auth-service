package com.bigtree.auth.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Document(collection = "partner-signups")
@Builder
public class PartnerSignup {

    @MongoId
    private String _id;
    private String name;
    private String email;
    private String mobile;
    private LocalDateTime dateReceived;
}
