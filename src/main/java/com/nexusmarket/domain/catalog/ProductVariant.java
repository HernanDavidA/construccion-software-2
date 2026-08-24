package com.nexusmarket.domain.catalog;

import java.util.Objects;

/**
 * Difference of a product (color, size, model, etc.).
 */
public class ProductVariant {

    private final String id;
    private String color;
    private String size;
    private String model;

    public ProductVariant(String id, String color, String size, String model) {
        this.id = requireText(id, "id");
        this.color = normalize(color);
        this.size = normalize(size);
        this.model = normalize(model);
        if (this.color.isEmpty() && this.size.isEmpty() && this.model.isEmpty()) {
            throw new IllegalArgumentException("the variant must specify color, size or model");
        }
    }

    public String getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = normalize(color);
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = normalize(size);
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = normalize(model);
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        return trimmed;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
