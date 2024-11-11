package com.bigtree.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartnerSignupRequest {
    private String name;
    private String email;
    private String mobile;
}
