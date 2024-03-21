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
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private UserType userType;

    public String getFullName(){
        return firstName+ " "+ lastName;
    }

}
