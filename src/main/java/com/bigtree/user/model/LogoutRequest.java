package com.bigtree.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutRequest {
    private String userId;
}
