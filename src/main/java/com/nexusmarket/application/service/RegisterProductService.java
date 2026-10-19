package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.RegisterProduct;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductStatus;
import com.nexusmarket.domain.model.ProductVariant;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.RegisterProductUseCase;
import com.nexusmarket.domain.port.outbound.ProductRepository;
import com.nexusmarket.domain.port.outbound.SellerRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RegisterProductService implements RegisterProductUseCase {

    private final AccessPolicy accessPolicy;
    private final ProductRepository products;
    private final SellerRepository sellers;

    public RegisterProductService(AccessPolicy accessPolicy, ProductRepository products, SellerRepository sellers) {
        this.accessPolicy = accessPolicy;
        this.products = products;
        this.sellers = sellers;
    }

    @Override
    public Product register(User actor, RegisterProduct command) {
        accessPolicy.requireRole(actor, Role.SELLER);
        Seller seller = sellerOf(actor);
        Product product = new Product(
                UUID.randomUUID().toString(),
                seller.getId(),
                command.name(),
                command.description(),
                command.type(),
                command.price(),
                identify(command.variants()),
                ProductStatus.SUSPENDED);
        return products.save(product);
    }

    private List<ProductVariant> identify(List<ProductVariant> variants) {
        List<ProductVariant> identified = new ArrayList<>();
        if (variants == null) {
            return identified;
        }
        for (ProductVariant variant : variants) {
            String id = variant.id() == null || variant.id().isBlank() ? UUID.randomUUID().toString() : variant.id();
            identified.add(new ProductVariant(id, variant.sku(), variant.color(), variant.size(), variant.model()));
        }
        return identified;
    }

    private Seller sellerOf(User actor) {
        return sellers.findByUserId(actor.getId()).orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
    }
}
