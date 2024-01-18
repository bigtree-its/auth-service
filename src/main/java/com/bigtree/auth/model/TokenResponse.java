package com.bigtree.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {

    private String idToken;
    private String accessToken;
    private Boolean success;
    private String message;
}
