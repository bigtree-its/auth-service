package com.bigtree.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetEmail {

    private String targetUrl;
    private String otp;
    private String email;
    private String name;
}
