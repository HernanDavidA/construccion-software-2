package com.nexusmarket.application.users.port.in;

import com.nexusmarket.domain.users.Seller;

public interface OnboardSellerUseCase {

    Seller execute(String administratorId, String sellerId, String identityDocument,
                   String fullName, String email, String businessName);
}
