package com.nexusmarket.domain.model;

public class Seller extends User {

    private final String tradeName;

    public Seller(
            String id,
            String fullName,
            String email,
            String identityDocument,
            UserStatus status,
            String tradeName) {
        super(id, fullName, email, identityDocument, status, Role.SELLER);
        this.tradeName = RequiredText.require(tradeName, "El nombre comercial es obligatorio");
    }

    public String getTradeName() {
        return tradeName;
    }
}
