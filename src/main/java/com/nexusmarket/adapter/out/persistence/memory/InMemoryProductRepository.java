package com.nexusmarket.adapter.out.persistence.memory;

import com.nexusmarket.application.catalog.port.out.ProductRepository;
import com.nexusmarket.domain.catalog.Product;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<String, Product> products = new LinkedHashMap<>();

    @Override
    public void save(Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }
}
