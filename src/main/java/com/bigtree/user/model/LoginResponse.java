package com.bigtree.user.model;

import com.bigtree.user.entity.Session;
import com.bigtree.user.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String email;
    private String firstName;
    private String lastName;
    private String userId;
    private String sessionId;
    private Boolean success;
    private String message;
}
