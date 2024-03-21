package com.bigtree.auth.model;

import com.bigtree.auth.entity.UserType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRequest {

    private String grantType;
    private String userId;
    private String clientSecret;
    private String clientAssertion;
    private String clientAssertionType;
    private String username;
    private String password;
    private UserType userType;
}
