package com.bigtree.user.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PasswordResetSubmit {

    private String email;
    private String otp;
    private String password;
}
