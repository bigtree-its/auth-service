package com.bigtree.auth.entity;

import lombok.Getter;

@Getter
public enum UserType {

    Customer("Customer", "cus"),
    Supplier("Supplier", "sup"),
    Employee("Employee", "emp"),
    CustomerApp("CustomerApp", "capp"),
    SupplierApp("SupplierApp", "sapp");

    private final String name;
    private final String code;

    UserType(String name, String code){
        this.name = name;
        this.code = code;
    }

    public static UserType fromName(String name) {
        for (UserType b : UserType.values()) {
            if (b.name.equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }

    public static UserType fromCode(String code) {
        for (UserType b : UserType.values()) {
            if (b.code.equalsIgnoreCase(code)) {
                return b;
            }
        }
        return null;
    }
}
