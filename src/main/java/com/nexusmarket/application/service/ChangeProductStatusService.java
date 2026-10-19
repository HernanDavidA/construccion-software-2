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
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class ChangeProductStatusService implements ChangeProductStatusUseCase {

    private final AccessPolicy accessPolicy;
    private final ProductRepository products;

    public ChangeProductStatusService(AccessPolicy accessPolicy, ProductRepository products) {
        this.accessPolicy = accessPolicy;
        this.products = products;
    }

    @Override
    public Product change(User actor, String productId, ProductStatus status) {
        accessPolicy.requireRole(actor, Role.SELLER);
        if (!(actor instanceof Seller seller)) {
            throw new ResourceNotFoundException("Vendedor no encontrado");
        }
        Product product = products.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (!product.belongsTo(seller.getId())) {
            throw new AccessDeniedException("RG-03: el vendedor solo administra sus productos");
        }
        product.changeStatus(status);
        return products.save(product);
    }
}
