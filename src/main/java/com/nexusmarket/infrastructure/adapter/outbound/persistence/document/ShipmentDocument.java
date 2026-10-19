package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Shipment;
import com.nexusmarket.domain.model.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shipments")
public class ShipmentDocument {

    @Id
    private String id;
    private String orderId;
    private AddressDocument deliveryAddress;
    private ShipmentStatus status;

    public static ShipmentDocument from(Shipment shipment) {
        return new ShipmentDocument(shipment.getId(), shipment.getOrderId(), AddressMapper.from(shipment.getDeliveryAddress()), shipment.getStatus());
    }

    public Shipment toDomain() {
        return new Shipment(id, orderId, AddressMapper.toDomain(deliveryAddress), status);
    }
}
