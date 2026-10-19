package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductStatus;
import com.nexusmarket.domain.model.ProductType;
import com.nexusmarket.domain.model.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class ProductDocument {

    @Id
    private String id;
    private String sellerId;
    private String name;
    private String description;
    private ProductType type;
    private BigDecimal price;
    private List<VariantDocument> variants;
    private ProductStatus status;

    public static ProductDocument from(Product product) {
        List<VariantDocument> stored = product.getVariants().stream()
                .map(variant -> new VariantDocument(variant.id(), variant.sku(), variant.color(), variant.size(), variant.model()))
                .toList();
        return new ProductDocument(product.getId(), product.getSellerId(), product.getName(), product.getDescription(), product.getType(), product.getPrice(), stored, product.getStatus());
    }

    public Product toDomain() {
        List<ProductVariant> domainVariants = variants == null
                ? List.of()
                : variants.stream().map(variant -> new ProductVariant(variant.getId(), variant.getSku(), variant.getColor(), variant.getSize(), variant.getModel())).toList();
        return new Product(id, sellerId, name, description, type, price, domainVariants, status);
    }
}
