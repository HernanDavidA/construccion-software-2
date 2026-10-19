package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.command.RegisterSeller;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.SellerOnboarding;
import com.nexusmarket.domain.port.inbound.QuerySellersUseCase;
import com.nexusmarket.domain.port.inbound.RegisterSellerUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final RegisterSellerUseCase registerSeller;
    private final QuerySellersUseCase querySellers;
    private final CurrentUser currentUser;

    public SellerController(RegisterSellerUseCase registerSeller, QuerySellersUseCase querySellers, CurrentUser currentUser) {
        this.registerSeller = registerSeller;
        this.querySellers = querySellers;
        this.currentUser = currentUser;
    }

    @PostMapping
    SellerOnboarding register(HttpServletRequest request, @RequestBody RegisterSeller command) {
        return registerSeller.register(currentUser.require(request), command);
    }

    @GetMapping("/{id}")
    Seller get(HttpServletRequest request, @PathVariable String id) {
        return querySellers.get(currentUser.require(request), id);
    }

    @GetMapping
    List<Seller> list(HttpServletRequest request) {
        return querySellers.list(currentUser.require(request));
    }
}
