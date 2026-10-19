package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.RegisterBuyer;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.BuyerCommercialStatus;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
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
        User user = registerUserService.register(command.fullName(), command.email(), command.identityDocument(), Role.BUYER);
        Buyer buyer = new Buyer(
                UUID.randomUUID().toString(),
                user.getId(),
                command.primaryAddress(),
                command.additionalAddresses(),
                BuyerCommercialStatus.ENABLED);
        return buyers.save(buyer);
    }
}
