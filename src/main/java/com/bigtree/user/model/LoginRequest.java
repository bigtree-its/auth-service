package com.bigtree.user.model;

import com.bigtree.user.entity.UserType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {

    private String email;
    private String password;
    private UserType userType;
}
