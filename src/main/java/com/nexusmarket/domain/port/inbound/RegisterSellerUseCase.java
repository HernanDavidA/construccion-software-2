package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.command.RegisterSeller;
import com.nexusmarket.domain.model.SellerOnboarding;
import com.nexusmarket.domain.model.User;

public interface RegisterSellerUseCase {
    SellerOnboarding register(User actor, RegisterSeller command);
}
