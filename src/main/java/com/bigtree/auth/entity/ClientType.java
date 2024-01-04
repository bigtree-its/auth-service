package com.bigtree.auth.entity;

public enum ClientType {

    Customer("Customer", "cus"),
    Supplier("Supplier", "sup"),
    Employee("Employee", "emp"),
    CustomerApp("CustomerApp", "capp"),
    SupplierApp("SupplierApp", "sapp");

    private final String name;
    private final String code;

    ClientType(String name, String code){
        this.name = name;
        this.code = code;
    }

    public String getName(){
        return this.name;
    }

    public String getCode(){
        return this.code;
    }

    public static ClientType fromName(String name) {
        for (ClientType b : ClientType.values()) {
            if (b.name.equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }

    public static ClientType fromCode(String code) {
        for (ClientType b : ClientType.values()) {
            if (b.code.equalsIgnoreCase(code)) {
                return b;
            }
        }
        return null;
    }
}
