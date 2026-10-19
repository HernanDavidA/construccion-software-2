package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductStatus;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.BrowseCatalogUseCase;
import com.nexusmarket.domain.port.outbound.ProductRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrowseCatalogService implements BrowseCatalogUseCase {

    private final AccessPolicy accessPolicy;
    private final ProductRepository products;

    public BrowseCatalogService(AccessPolicy accessPolicy, ProductRepository products) {
        this.accessPolicy = accessPolicy;
        this.products = products;
    }

    @Override
    public Product get(String productId) {
        return products.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    @Override
    public List<Product> published() {
        return products.findByStatus(ProductStatus.PUBLISHED);
    }

    @Override
    public List<Product> listOwn(User actor) {
        accessPolicy.requireRole(actor, Role.SELLER);
        if (!(actor instanceof Seller seller)) {
            throw new ResourceNotFoundException("Vendedor no encontrado");
        }
        return products.findBySellerId(seller.getId());
    }
}
