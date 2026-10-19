package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.RegisterStaff;
import com.nexusmarket.domain.exception.BusinessRuleException;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.RegisterStaffUseCase;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

@Service
public class RegisterStaffService implements RegisterStaffUseCase {

    private static final Set<Role> STAFF_ROLES = EnumSet.of(Role.LOGISTICS_OPERATOR, Role.ADMINISTRATOR, Role.SUPERVISOR);

    private final AccessPolicy accessPolicy;
    private final RegisterUserService registerUserService;

    public RegisterStaffService(AccessPolicy accessPolicy, RegisterUserService registerUserService) {
        this.accessPolicy = accessPolicy;
        this.registerUserService = registerUserService;
    }

    @Override
    public User register(User actor, RegisterStaff command) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR);
        if (command == null || !STAFF_ROLES.contains(command.role())) {
            throw new BusinessRuleException("El colaborador debe ser operador, administrador o supervisor");
        }
        return registerUserService.register(command.fullName(), command.email(), command.identityDocument(), command.role());
    }
}
