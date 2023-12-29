package com.bigtree.auth.model;

public enum GrantType {

    // enum constants calling the enum constructors
    CLIENT_CREDENTIALS("client_credentials"),
    PASSWORD("password");

    private final String name;

    // private enum constructor
    private GrantType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
