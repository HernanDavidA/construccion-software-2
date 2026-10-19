package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.BuyerCommercialStatus;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.ChangeBuyerCommercialStatusUseCase;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class ChangeBuyerCommercialStatusService implements ChangeBuyerCommercialStatusUseCase {

    private final AccessPolicy accessPolicy;
    private final BuyerRepository buyers;

    public ChangeBuyerCommercialStatusService(AccessPolicy accessPolicy, BuyerRepository buyers) {
        this.accessPolicy = accessPolicy;
        this.buyers = buyers;
    }

    @Override
    public Buyer change(User actor, String buyerId, BuyerCommercialStatus status) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR);
        Buyer buyer = buyers.findById(buyerId).orElseThrow(() -> new ResourceNotFoundException("Comprador no encontrado"));
        buyer.changeCommercialStatus(status);
        return buyers.save(buyer);
    }
}
