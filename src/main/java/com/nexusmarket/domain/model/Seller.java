package com.nexusmarket.domain.model;

public class Seller {

    private final String id;
    private final String userId;
    private final String tradeName;
    private final String identityDocument;

    public Seller(String id, String userId, String tradeName, String identityDocument) {
        this.id = RequiredText.require(id, "El identificador del vendedor es obligatorio");
        this.userId = RequiredText.require(userId, "El vendedor debe estar asociado a un usuario");
        this.tradeName = RequiredText.require(tradeName, "El nombre comercial es obligatorio");
        this.identityDocument = RequiredText.require(identityDocument, "El documento de identidad del vendedor es obligatorio");
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public String getIdentityDocument() {
        return identityDocument;
    }
}
