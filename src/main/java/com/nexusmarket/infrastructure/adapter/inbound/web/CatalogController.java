package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.command.RegisterProduct;
import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductStatus;
import com.nexusmarket.domain.port.inbound.BrowseCatalogUseCase;
import com.nexusmarket.domain.port.inbound.ChangeProductStatusUseCase;
import com.nexusmarket.domain.port.inbound.RegisterProductUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CatalogController {

    private final RegisterProductUseCase registerProduct;
    private final ChangeProductStatusUseCase changeProductStatus;
    private final BrowseCatalogUseCase browseCatalog;
    private final CurrentUser currentUser;

    public CatalogController(
            RegisterProductUseCase registerProduct,
            ChangeProductStatusUseCase changeProductStatus,
            BrowseCatalogUseCase browseCatalog,
            CurrentUser currentUser) {
        this.registerProduct = registerProduct;
        this.changeProductStatus = changeProductStatus;
        this.browseCatalog = browseCatalog;
        this.currentUser = currentUser;
    }

    @PostMapping("/api/products")
    Product register(HttpServletRequest request, @RequestBody RegisterProduct command) {
        return registerProduct.register(currentUser.require(request), command);
    }

    @PatchMapping("/api/products/{id}/status")
    Product changeStatus(HttpServletRequest request, @PathVariable String id, @RequestParam ProductStatus status) {
        return changeProductStatus.change(currentUser.require(request), id, status);
    }

    @GetMapping("/api/products/mine")
    List<Product> mine(HttpServletRequest request) {
        return browseCatalog.listOwn(currentUser.require(request));
    }

    @GetMapping("/api/catalog")
    List<Product> catalog() {
        return browseCatalog.published();
    }

    @GetMapping("/api/products/{id}")
    Product get(@PathVariable String id) {
        return browseCatalog.get(id);
    }
}
