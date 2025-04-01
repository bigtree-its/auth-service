package com.bigtree.auth.entity;

import lombok.Getter;

@Getter
public enum BusinessType {

    CloudKitchen("CloudKitchen"),
    Fashion("DeliveryPartner");

    private final String name;

    BusinessType(String name){
        this.name = name;
    }

    public static BusinessType fromName(String name) {
        for (BusinessType b : BusinessType.values()) {
            if (b.name.equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }

}
