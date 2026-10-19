package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.BusinessRuleException;
import com.nexusmarket.domain.model.EmailAddress;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.UserStatus;
import com.nexusmarket.domain.port.outbound.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegisterUserService {

    private final UserRepository users;

    public RegisterUserService(UserRepository users) {
        this.users = users;
    }

    public User register(String fullName, String email, String identityDocument, Role role) {
        String normalizedEmail = EmailAddress.normalize(email);
        String document = identityDocument == null ? "" : identityDocument.trim();
        if (users.existsByEmail(normalizedEmail)) {
            throw new BusinessRuleException("El correo electrónico debe ser único");
        }
        if (document.isBlank() || users.existsByIdentityDocument(document)) {
            throw new BusinessRuleException("El documento de identidad debe ser único");
        }
        return users.save(new User(UUID.randomUUID().toString(), fullName, normalizedEmail, document, role, UserStatus.ACTIVE));
    }
}
