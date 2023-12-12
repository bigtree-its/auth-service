package com.bigtree.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String idToken;
    private String accessToken;
    private Boolean success;
    private String message;
}
