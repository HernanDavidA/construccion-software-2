package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.command.AddCartItem;
import com.nexusmarket.domain.model.Cart;
import com.nexusmarket.domain.port.inbound.ManageCartUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final ManageCartUseCase cart;
    private final CurrentUser currentUser;

    public CartController(ManageCartUseCase cart, CurrentUser currentUser) {
        this.cart = cart;
        this.currentUser = currentUser;
    }

    @GetMapping
    Cart view(HttpServletRequest request) {
        return cart.view(currentUser.require(request));
    }

    @PostMapping("/items")
    Cart add(HttpServletRequest request, @RequestBody AddCartItem command) {
        return cart.addItem(currentUser.require(request), command);
    }

    @DeleteMapping("/items")
    Cart remove(HttpServletRequest request, @RequestParam String productId, @RequestParam(required = false) String variantId) {
        return cart.removeItem(currentUser.require(request), productId, variantId);
    }

    @DeleteMapping
    Cart clear(HttpServletRequest request) {
        return cart.clear(currentUser.require(request));
    }
}
