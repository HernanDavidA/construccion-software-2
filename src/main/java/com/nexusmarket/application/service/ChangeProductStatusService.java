package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductStatus;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.ChangeProductStatusUseCase;
import com.nexusmarket.domain.port.outbound.ProductRepository;
import com.nexusmarket.domain.port.outbound.SellerRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class ChangeProductStatusService implements ChangeProductStatusUseCase {

    private final AccessPolicy accessPolicy;
    private final ProductRepository products;
    private final SellerRepository sellers;

    public ChangeProductStatusService(AccessPolicy accessPolicy, ProductRepository products, SellerRepository sellers) {
        this.accessPolicy = accessPolicy;
        this.products = products;
        this.sellers = sellers;
    }

    @Override
    public Product change(User actor, String productId, ProductStatus status) {
        accessPolicy.requireRole(actor, Role.SELLER);
        Product product = products.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        Seller seller = sellers.findByUserId(actor.getId()).orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
        if (!product.belongsTo(seller.getId())) {
            throw new AccessDeniedException("RG-03: el vendedor solo administra sus productos");
        }
        product.changeStatus(status);
        return products.save(product);
    }
}
