package com.nexusmarket.domain.users;

/**
 * Onboards sellers and warehouses. Sellers cannot self-register.
 */
public class Administrator extends User {

    public Administrator(String id, String identityDocument, String fullName,
                         String email, UserStatus status) {
        super(id, identityDocument, fullName, email, Role.ADMINISTRATOR, status);
    }

    public Seller onboardSeller(String id, String identityDocument, String fullName,
                                String email, String businessName) {
        if (!isActive()) {
            throw new IllegalStateException("The administrator is not active");
        }
        return new Seller(id, identityDocument, fullName, email,
                UserStatus.ACTIVE, businessName, SellerStatus.ACTIVE);
    }
}
