package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.User;

import java.util.List;

public interface BrowseCatalogUseCase {
    Product get(String productId);

    List<Product> published();

    List<Product> listOwn(User actor);
}
