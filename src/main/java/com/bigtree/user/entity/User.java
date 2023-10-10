package com.bigtree.user.entity;

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
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private UserType userType;

}
