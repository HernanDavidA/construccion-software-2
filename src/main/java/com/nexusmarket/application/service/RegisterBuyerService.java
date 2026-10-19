package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.RegisterBuyer;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.BuyerCommercialStatus;
import com.nexusmarket.domain.model.UserStatus;
import com.nexusmarket.domain.port.inbound.RegisterBuyerUseCase;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegisterBuyerService implements RegisterBuyerUseCase {

    private final BuyerRepository buyers;
    private final RegisterUserService registerUserService;

    public RegisterBuyerService(BuyerRepository buyers, RegisterUserService registerUserService) {
        this.buyers = buyers;
        this.registerUserService = registerUserService;
    }

    @Override
    public Buyer register(RegisterBuyer command) {
        registerUserService.requireAvailableIdentity(command.email(), command.identityDocument());
        Buyer buyer = new Buyer(
                UUID.randomUUID().toString(),
                command.fullName(),
                command.email(),
                command.identityDocument(),
                UserStatus.ACTIVE,
                command.primaryAddress(),
                command.additionalAddresses(),
                BuyerCommercialStatus.ENABLED);
        return buyers.save(buyer);
    }
}
