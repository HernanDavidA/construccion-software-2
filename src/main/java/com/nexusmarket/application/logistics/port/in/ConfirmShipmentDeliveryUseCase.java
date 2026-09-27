package com.nexusmarket.application.logistics.port.in;

import com.nexusmarket.domain.logistics.Shipment;

public interface ConfirmShipmentDeliveryUseCase {

    Shipment execute(String shipmentId);
}
