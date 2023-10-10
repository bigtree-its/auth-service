package com.bigtree.user.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {

    String addressLine1;
    String addressLine2;
    String city;
    String postcode;
    String country;
    String latitude;
    String longitude;
}
