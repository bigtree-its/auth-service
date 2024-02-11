package com.bigtree.auth.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "contacts")
@Builder
@Data
public class Contacts {
    @MongoId
    private String _id;
    private String about;
    private String fullName;
    private String email;
    private String mobile;
    private String message;
    private LocalDate date;
    private boolean responded;
}
