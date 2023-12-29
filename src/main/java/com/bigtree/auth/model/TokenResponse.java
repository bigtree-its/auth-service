package com.bigtree.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {

    private String id_token;
    private String access_token;
    private Boolean success;
    private String message;
}
