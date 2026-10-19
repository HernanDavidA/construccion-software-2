package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.UpdateAddresses;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.UpdateBuyerAddressesUseCase;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class UpdateBuyerAddressesService implements UpdateBuyerAddressesUseCase {

    private final AccessPolicy accessPolicy;
    private final BuyerRepository buyers;

    public UpdateBuyerAddressesService(AccessPolicy accessPolicy, BuyerRepository buyers) {
        this.accessPolicy = accessPolicy;
        this.buyers = buyers;
    }

    @Override
    public Buyer update(User actor, UpdateAddresses command) {
        accessPolicy.requireRole(actor, Role.BUYER);
        if (!(actor instanceof Buyer buyer)) {
            throw new ResourceNotFoundException("Comprador no encontrado");
        }
        buyer.updateAddresses(command.primaryAddress(), command.additionalAddresses());
        return buyers.save(buyer);
    }
}
