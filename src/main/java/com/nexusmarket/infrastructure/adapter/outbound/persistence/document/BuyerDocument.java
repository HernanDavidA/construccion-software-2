package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Address;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.BuyerCommercialStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "buyers")
public class BuyerDocument {

    @Id
    private String id;
    private String userId;
    private AddressDocument primaryAddress;
    private List<AddressDocument> additionalAddresses;
    private BuyerCommercialStatus commercialStatus;

    public static BuyerDocument from(Buyer buyer) {
        return new BuyerDocument(
                buyer.getId(),
                buyer.getUserId(),
                AddressMapper.from(buyer.getPrimaryAddress()),
                buyer.getAdditionalAddresses().stream().map(AddressMapper::from).toList(),
                buyer.getCommercialStatus());
    }

    public Buyer toDomain() {
        List<Address> additional = additionalAddresses == null ? List.of() : additionalAddresses.stream().map(AddressMapper::toDomain).toList();
        return new Buyer(id, userId, AddressMapper.toDomain(primaryAddress), additional, commercialStatus);
    }
}
