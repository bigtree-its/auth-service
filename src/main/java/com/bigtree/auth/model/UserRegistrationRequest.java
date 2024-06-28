package com.bigtree.auth.model;

import com.bigtree.auth.entity.BusinessType;
import com.bigtree.auth.entity.UserType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistrationRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String password;
    private UserType userType;
    private BusinessType businessType;

}
