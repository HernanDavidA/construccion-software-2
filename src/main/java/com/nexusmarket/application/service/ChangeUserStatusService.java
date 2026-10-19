package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.UserStatus;
import com.nexusmarket.domain.port.inbound.ChangeUserStatusUseCase;
import com.nexusmarket.domain.port.outbound.UserRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class ChangeUserStatusService implements ChangeUserStatusUseCase {

    private final AccessPolicy accessPolicy;
    private final UserRepository users;

    public ChangeUserStatusService(AccessPolicy accessPolicy, UserRepository users) {
        this.accessPolicy = accessPolicy;
        this.users = users;
    }

    @Override
    public User change(User actor, String userId, UserStatus status) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR);
        User user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.changeStatus(status);
        return users.save(user);
    }
}
