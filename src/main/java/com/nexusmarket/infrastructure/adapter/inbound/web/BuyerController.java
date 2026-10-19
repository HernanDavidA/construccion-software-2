package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.command.RegisterBuyer;
import com.nexusmarket.domain.command.UpdateAddresses;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.BuyerCommercialStatus;
import com.nexusmarket.domain.port.inbound.ChangeBuyerCommercialStatusUseCase;
import com.nexusmarket.domain.port.inbound.QueryBuyersUseCase;
import com.nexusmarket.domain.port.inbound.RegisterBuyerUseCase;
import com.nexusmarket.domain.port.inbound.UpdateBuyerAddressesUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/buyers")
public class BuyerController {

    private final RegisterBuyerUseCase registerBuyer;
    private final UpdateBuyerAddressesUseCase updateAddresses;
    private final ChangeBuyerCommercialStatusUseCase changeStatus;
    private final QueryBuyersUseCase queryBuyers;
    private final CurrentUser currentUser;

    public BuyerController(
            RegisterBuyerUseCase registerBuyer,
            UpdateBuyerAddressesUseCase updateAddresses,
            ChangeBuyerCommercialStatusUseCase changeStatus,
            QueryBuyersUseCase queryBuyers,
            CurrentUser currentUser) {
        this.registerBuyer = registerBuyer;
        this.updateAddresses = updateAddresses;
        this.changeStatus = changeStatus;
        this.queryBuyers = queryBuyers;
        this.currentUser = currentUser;
    }

    @PostMapping
    Buyer register(@RequestBody RegisterBuyer command) {
        return registerBuyer.register(command);
    }

    @PutMapping("/addresses")
    Buyer updateAddresses(HttpServletRequest request, @RequestBody UpdateAddresses command) {
        return updateAddresses.update(currentUser.require(request), command);
    }

    @PatchMapping("/{id}/commercial-status")
    Buyer changeStatus(HttpServletRequest request, @PathVariable String id, @RequestParam BuyerCommercialStatus status) {
        return changeStatus.change(currentUser.require(request), id, status);
    }

    @GetMapping("/me")
    Buyer own(HttpServletRequest request) {
        return queryBuyers.getOwn(currentUser.require(request));
    }

    @GetMapping("/{id}")
    Buyer get(HttpServletRequest request, @PathVariable String id) {
        return queryBuyers.get(currentUser.require(request), id);
    }

    @GetMapping
    List<Buyer> list(HttpServletRequest request) {
        return queryBuyers.list(currentUser.require(request));
    }
}
