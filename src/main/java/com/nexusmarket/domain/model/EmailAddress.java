package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

import java.util.Locale;
import java.util.regex.Pattern;

public final class EmailAddress {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s.]+(\\.[^@\\s.]+)+$");

    private EmailAddress() {
    }

    public static String normalize(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessRuleException("El correo electrónico es obligatorio y debe ser válido");
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL.matcher(normalized).matches()) {
            throw new BusinessRuleException("El correo electrónico es obligatorio y debe ser válido");
        }
        return normalized;
    }
}
