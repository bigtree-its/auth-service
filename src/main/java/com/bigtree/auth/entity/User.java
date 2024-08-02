package com.bigtree.auth.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "users")
@Builder
@Data
public class User {

    @MongoId
    private String _id;
    private String userId;
    private String name;
    private String email;
    private String mobile;
    private String businessId;
    private UserType userType;
    private String businessType;

}
