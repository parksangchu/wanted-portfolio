package com.wanted.portfolio.member.model;

public enum Role {
    USER, ADMIN;

    private static final String PREFIX = "ROLE_";

    public String getValue() {
        return PREFIX + this.name();
    }

    public static Role from(String value) {
        String name = value.replace(PREFIX, "");
        return valueOf(name);
    }
}
