package com.bigtree.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    
    String endpoint;
    String message;

}
