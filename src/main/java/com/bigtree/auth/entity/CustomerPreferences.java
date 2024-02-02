package com.bigtree.auth.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "customer_preferences")
@Builder
public class CustomerPreferences {

    @MongoId
    private String _id;
    private String userId;
    private Boolean communicationViaEmail;
    private Boolean communicationViaMobile;
    private List<String> cuisines;
    private List<String> chefs;
    private List<String> foods;
}
