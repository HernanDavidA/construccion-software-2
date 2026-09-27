package com.nexusmarket.application.catalog.port.out;

import com.nexusmarket.domain.catalog.Product;

import java.util.Optional;

public interface ProductRepository {

    void save(Product product);

    Optional<Product> findById(String id);
}
