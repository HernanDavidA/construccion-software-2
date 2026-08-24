package com.nexusmarket.domain.users;

import java.util.Objects;

/**
 * Base identity of any marketplace participant (OBJ-01, RG-01, RG-02).
 * The role is fixed in the subclass and cannot be changed.
 */
public abstract class User {

    private final String id;
    private final String identityDocument;
    private String fullName;
    private final String email;
    private final Role role;
    private UserStatus status;

    protected User(String id, String identityDocument, String fullName,
                   String email, Role role, UserStatus status) {
        this.id = requireText(id, "id");
        this.identityDocument = requireText(identityDocument, "identityDocument");
        this.fullName = requireText(fullName, "fullName");
        this.email = requireEmail(email);
        this.role = Objects.requireNonNull(role, "role is required");
        this.status = Objects.requireNonNull(status, "status is required");
    }

    public String getId() {
        return id;
    }

    public String getIdentityDocument() {
        return identityDocument;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = requireText(fullName, "fullName");
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void block() {
        this.status = UserStatus.BLOCKED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    protected static String requireText(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        return trimmed;
    }

    private static String requireEmail(String email) {
        String value = requireText(email, "email");
        if (!value.contains("@")) {
            throw new IllegalArgumentException("email is not valid");
        }
        return value.toLowerCase();
    }
}
