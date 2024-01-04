package com.bigtree.auth.model;

import com.bigtree.auth.entity.ClientType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequest {

    private String grantType;
    private String clientId;
    private String clientEmail;
    private String clientSecret;
    private String clientAssertion;
    private String clientAssertionType;
    private String username;
    private String password;
    private ClientType clientType;
}
