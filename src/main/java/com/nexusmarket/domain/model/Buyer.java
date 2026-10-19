package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

import java.util.ArrayList;
import java.util.List;

public class Buyer extends User {

    private Address primaryAddress;
    private final List<Address> additionalAddresses = new ArrayList<>();
    private BuyerCommercialStatus commercialStatus;

    public Buyer(
            String id,
            String fullName,
            String email,
            String identityDocument,
            UserStatus status,
            Address primaryAddress,
            List<Address> additionalAddresses,
            BuyerCommercialStatus commercialStatus) {
        super(id, fullName, email, identityDocument, status, Role.BUYER);
        replaceAddresses(primaryAddress, additionalAddresses);
        if (commercialStatus == null) {
            throw new BusinessRuleException("El estado comercial del comprador es obligatorio");
        }
        this.commercialStatus = commercialStatus;
    }

    public void updateAddresses(Address primaryAddress, List<Address> additionalAddresses) {
        replaceAddresses(primaryAddress, additionalAddresses);
    }

    public void changeCommercialStatus(BuyerCommercialStatus newStatus) {
        if (newStatus == null) {
            throw new BusinessRuleException("El estado comercial del comprador es obligatorio");
        }
        this.commercialStatus = newStatus;
    }

    private void replaceAddresses(Address primary, List<Address> additional) {
        if (primary == null || primary.line() == null || primary.line().isBlank()
                || primary.city() == null || primary.city().isBlank()) {
            throw new BusinessRuleException("La dirección principal es obligatoria");
        }
        this.primaryAddress = primary;
        this.additionalAddresses.clear();
        if (additional != null) {
            this.additionalAddresses.addAll(additional);
        }
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public List<Address> getAdditionalAddresses() {
        return List.copyOf(additionalAddresses);
    }

    public BuyerCommercialStatus getCommercialStatus() {
        return commercialStatus;
    }
}
