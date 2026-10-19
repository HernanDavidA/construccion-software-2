package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.BuyerCommercialStatus;
import com.nexusmarket.domain.model.User;

public interface ChangeBuyerCommercialStatusUseCase {
    Buyer change(User actor, String buyerId, BuyerCommercialStatus status);
}
