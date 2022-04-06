package com.example.storageit.persistence.entity;

public enum UserRole {
    INDIVIDUAL(Roles.INDIVIDUAL),
    ADMIN(Roles.ADMIN),
    BUSINESS(Roles.BUSINESS);

    public interface Roles {
        public static final String INDIVIDUAL = "INDIVIDUAL";
        public static final String ADMIN = "ADMIN";
        public static final String BUSINESS = "BUSINESS";
    }

    private final String label;

    private UserRole(String label) {
        this.label = label;
    }

    public String toString() {
        return this.label;
    }
}
