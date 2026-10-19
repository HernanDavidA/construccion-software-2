package com.nexusmarket.domain.model;

import java.util.Objects;

public final class VariantIds {

    private VariantIds() {
    }

    public static String normalize(String variantId) {
        if (variantId == null || variantId.isBlank()) {
            return null;
        }
        return variantId.trim();
    }

    public static boolean same(String first, String second) {
        return Objects.equals(normalize(first), normalize(second));
    }
}
