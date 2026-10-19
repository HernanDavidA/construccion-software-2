package com.nexusmarket.infrastructure.config;

import com.nexusmarket.domain.model.EmailAddress;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.UserStatus;
import com.nexusmarket.domain.port.outbound.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "nexusmarket.admin.auto-create", havingValue = "true")
public class InitialAdminLoader implements ApplicationRunner {

    private final UserRepository users;
    private final String id;
    private final String name;
    private final String email;
    private final String identityDocument;

    public InitialAdminLoader(
            UserRepository users,
            @Value("${nexusmarket.admin.id}") String id,
            @Value("${nexusmarket.admin.name}") String name,
            @Value("${nexusmarket.admin.email}") String email,
            @Value("${nexusmarket.admin.identity-document}") String identityDocument) {
        this.users = users;
        this.id = id;
        this.name = name;
        this.email = email;
        this.identityDocument = identityDocument;
    }

    @Override
    public void run(ApplicationArguments args) {
        String normalizedEmail = EmailAddress.normalize(email);
        if (users.findById(id).isPresent() || users.existsByEmail(normalizedEmail) || users.existsByIdentityDocument(identityDocument)) {
            return;
        }
        users.save(new User(id, name, normalizedEmail, identityDocument, Role.ADMINISTRATOR, UserStatus.ACTIVE));
    }
}
