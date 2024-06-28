package com.bigtree.auth.entity;

import lombok.Getter;

@Getter
public enum UserType {

    Admin("Admin"),
    Customer("Customer"),
    Business("Business"),
    Employee("Employee"),
    CustomerApp("CustomerApp"),
    SupplierApp("SupplierApp");

    private final String name;

    UserType(String name){
        this.name = name;
    }

    public static UserType fromName(String name) {
        for (UserType b : UserType.values()) {
            if (b.name.equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }
}
