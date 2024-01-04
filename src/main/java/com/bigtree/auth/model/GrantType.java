package com.bigtree.auth.model;

import lombok.Getter;

@Getter
public enum GrantType {

    // enum constants calling the enum constructors
    CLIENT_CREDENTIALS("client_credentials"),
    PASSWORD("password");

    private final String name;

    // private enum constructor
    private GrantType(String name) {
        this.name = name;
    }

}
