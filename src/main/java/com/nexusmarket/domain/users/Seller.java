package com.nexusmarket.domain.users;

import java.util.Objects;

/**
 * Product supplier. Cannot self-register: an {@link Administrator} onboards them (OBJ-02).
 */
public class Seller extends User {

    private String businessName;
    private SellerStatus sellerStatus;

    public Seller(String id, String identityDocument, String fullName,
                  String email, UserStatus status, String businessName,
                  SellerStatus sellerStatus) {
        super(id, identityDocument, fullName, email, Role.SELLER, status);
        this.businessName = requireText(businessName, "businessName");
        this.sellerStatus = Objects.requireNonNull(sellerStatus, "sellerStatus is required");
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = requireText(businessName, "businessName");
    }

    public SellerStatus getSellerStatus() {
        return sellerStatus;
    }

    public void setSellerStatus(SellerStatus sellerStatus) {
        this.sellerStatus = Objects.requireNonNull(sellerStatus, "sellerStatus is required");
    }
}
