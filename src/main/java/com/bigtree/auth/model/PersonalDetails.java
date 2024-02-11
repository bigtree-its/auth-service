package com.bigtree.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonalDetails {

    private String customerId;
    private String mobile;
    private String firstName;
    private String lastName;
}
