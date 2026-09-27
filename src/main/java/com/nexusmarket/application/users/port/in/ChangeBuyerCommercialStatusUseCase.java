package com.nexusmarket.application.users.port.in;

import com.nexusmarket.domain.users.Buyer;
import com.nexusmarket.domain.users.CommercialStatus;

public interface ChangeBuyerCommercialStatusUseCase {

    Buyer execute(String buyerId, CommercialStatus commercialStatus);
}
