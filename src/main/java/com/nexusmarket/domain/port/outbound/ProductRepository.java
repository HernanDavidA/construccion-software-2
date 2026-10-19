package com.nexusmarket.domain.port.outbound;

import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductStatus;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(String id);

    List<Product> findAll();

    List<Product> findByStatus(ProductStatus status);

    List<Product> findBySellerId(String sellerId);
}
