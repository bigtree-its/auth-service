package com.bigtree.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetSubmit {

    private String email;
    private String otp;
    private String password;
}
