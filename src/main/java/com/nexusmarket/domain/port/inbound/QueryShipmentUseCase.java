package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Shipment;
import com.nexusmarket.domain.model.User;

public interface QueryShipmentUseCase {
    Shipment getByOrder(User actor, String orderId);
}
