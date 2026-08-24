package com.nexusmarket.domain.users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * User who purchases products. Does not manage other buyers or inventory (OBJ-03).
 */
public class Buyer extends User {

    private Address primaryAddress;
    private final List<Address> additionalAddresses;
    private CommercialStatus commercialStatus;

    public Buyer(String id, String identityDocument, String fullName,
                 String email, UserStatus status, Address primaryAddress,
                 CommercialStatus commercialStatus) {
        super(id, identityDocument, fullName, email, Role.BUYER, status);
        this.primaryAddress = Objects.requireNonNull(primaryAddress, "primaryAddress is required");
        this.additionalAddresses = new ArrayList<>();
        this.commercialStatus = Objects.requireNonNull(commercialStatus, "commercialStatus is required");
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = Objects.requireNonNull(primaryAddress, "primaryAddress is required");
    }

    public List<Address> getAdditionalAddresses() {
        return Collections.unmodifiableList(additionalAddresses);
    }

    public void addAdditionalAddress(Address address) {
        additionalAddresses.add(Objects.requireNonNull(address, "address is required"));
    }

    public CommercialStatus getCommercialStatus() {
        return commercialStatus;
    }

    public void setCommercialStatus(CommercialStatus commercialStatus) {
        this.commercialStatus = Objects.requireNonNull(commercialStatus, "commercialStatus is required");
    }

    public boolean canBuy() {
        return isActive() && commercialStatus == CommercialStatus.ENABLED;
    }
}
