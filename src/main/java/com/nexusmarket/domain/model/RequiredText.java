package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

public final class RequiredText {

    private RequiredText() {
    }

    public static String require(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(message);
        }
        return value.trim();
    }
}
