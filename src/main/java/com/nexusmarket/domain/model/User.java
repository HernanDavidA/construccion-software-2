package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

public class User {

    private final String id;
    private final String fullName;
    private final String email;
    private final String identityDocument;
    private final Role role;
    private UserStatus status;

    public User(String id, String fullName, String email, String identityDocument, Role role, UserStatus status) {
        this(id, fullName, email, identityDocument, status, staffRole(role));
    }

    protected User(String id, String fullName, String email, String identityDocument, UserStatus status, Role role) {
        this.id = RequiredText.require(id, "El identificador del usuario es obligatorio");
        this.fullName = RequiredText.require(fullName, "El nombre completo es obligatorio");
        this.email = EmailAddress.normalize(email);
        this.identityDocument = RequiredText.require(identityDocument, "El documento de identidad es obligatorio");
        if (role == null) {
            throw new BusinessRuleException("RG-02: cada usuario debe tener un único rol");
        }
        if (status == null) {
            throw new BusinessRuleException("El estado del usuario es obligatorio");
        }
        this.role = role;
        this.status = status;
    }

    private static Role staffRole(Role role) {
        if (role == Role.BUYER || role == Role.SELLER) {
            throw new BusinessRuleException("El comprador y el vendedor no se crean como usuario genérico");
        }
        return role;
    }

    public void changeStatus(UserStatus newStatus) {
        if (newStatus == null) {
            throw new BusinessRuleException("El estado del usuario es obligatorio");
        }
        this.status = newStatus;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getIdentityDocument() {
        return identityDocument;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }
}
