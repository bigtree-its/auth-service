package com.bigtree.auth.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "identities")
@Builder
@Data
public class Identity {

    @MongoId
    private String _id;
    private String clientId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private ClientType clientType;

    public String getFullName(){
        return firstName+ " "+ lastName;
    }

}
