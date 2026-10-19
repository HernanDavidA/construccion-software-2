package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.QueryUsersUseCase;
import com.nexusmarket.domain.port.outbound.UserRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryUsersService implements QueryUsersUseCase {

    private final AccessPolicy accessPolicy;
    private final UserRepository users;

    public QueryUsersService(AccessPolicy accessPolicy, UserRepository users) {
        this.accessPolicy = accessPolicy;
        this.users = users;
    }

    @Override
    public User get(User actor, String userId) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR, Role.SUPERVISOR, Role.BUYER, Role.SELLER, Role.LOGISTICS_OPERATOR);
        User user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        boolean privileged = actor.getRole() == Role.ADMINISTRATOR || actor.getRole() == Role.SUPERVISOR;
        if (!privileged && !actor.getId().equals(user.getId())) {
            throw new AccessDeniedException("RG-03: el participante no puede administrar información fuera de su rol");
        }
        return user;
    }

    @Override
    public List<User> list(User actor) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR, Role.SUPERVISOR);
        return users.findAll();
    }
}
