package com.bigtree.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivateAccountRequest {

    private String accountId;
    private String activationCode;

}
