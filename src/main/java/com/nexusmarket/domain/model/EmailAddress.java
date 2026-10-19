package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

import java.util.Locale;

public final class EmailAddress {

    private EmailAddress() {
    }

    public static String normalize(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new BusinessRuleException("El correo electrónico es obligatorio y debe ser válido");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
