package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Address;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.BuyerCommercialStatus;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class UserDocument {

    @Id
    private String id;
    private String fullName;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String identityDocument;
    private Role role;
    private UserStatus status;
    private AddressDocument primaryAddress;
    private List<AddressDocument> additionalAddresses;
    private BuyerCommercialStatus commercialStatus;
    private String tradeName;

    public static UserDocument from(User user) {
        UserDocument document = new UserDocument();
        document.id = user.getId();
        document.fullName = user.getFullName();
        document.email = user.getEmail();
        document.identityDocument = user.getIdentityDocument();
        document.role = user.getRole();
        document.status = user.getStatus();
        if (user instanceof Buyer buyer) {
            document.primaryAddress = AddressMapper.from(buyer.getPrimaryAddress());
            document.additionalAddresses = buyer.getAdditionalAddresses().stream().map(AddressMapper::from).toList();
            document.commercialStatus = buyer.getCommercialStatus();
        }
        if (user instanceof Seller seller) {
            document.tradeName = seller.getTradeName();
        }
        return document;
    }

    public User toDomain() {
        if (role == Role.BUYER) {
            List<Address> additional = additionalAddresses == null
                    ? List.of()
                    : additionalAddresses.stream().map(AddressMapper::toDomain).toList();
            return new Buyer(id, fullName, email, identityDocument, status, AddressMapper.toDomain(primaryAddress), additional, commercialStatus);
        }
        if (role == Role.SELLER) {
            return new Seller(id, fullName, email, identityDocument, status, tradeName);
        }
        return new User(id, fullName, email, identityDocument, role, status);
    }
}
