package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Seller;
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
@Document(collection = "sellers")
public class SellerDocument {

    @Id
    private String id;
    private String userId;
    private String tradeName;
    private String identityDocument;

    public static SellerDocument from(Seller seller) {
        return new SellerDocument(seller.getId(), seller.getUserId(), seller.getTradeName(), seller.getIdentityDocument());
    }

    public Seller toDomain() {
        return new Seller(id, userId, tradeName, identityDocument);
    }
}
