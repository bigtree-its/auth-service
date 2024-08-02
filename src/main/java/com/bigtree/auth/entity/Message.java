package com.bigtree.auth.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Document(collection = "messages")
@Builder
@Data
public class Message {
    @MongoId
    private String _id;
    private String about;
    private String fullName;
    private String email;
    private String mobile;
    private String content;
    private LocalDate date;
    private boolean responded;
}
