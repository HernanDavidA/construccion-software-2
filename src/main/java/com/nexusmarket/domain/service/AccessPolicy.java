package com.nexusmarket.domain.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.BusinessRuleException;
import com.nexusmarket.domain.exception.UnauthenticatedException;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.UserStatus;

public final class AccessPolicy {

    public void requireRole(User actor, Role... allowedRoles) {
        if (actor == null) {
            throw new UnauthenticatedException("RG-01: toda operación debe ejecutarse por un usuario autenticado");
        }
        if (actor.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedException("El usuario está bloqueado");
        }
        if (actor.getRole() == null) {
            throw new BusinessRuleException("RG-02: cada usuario debe tener un único rol");
        }
        for (Role role : allowedRoles) {
            if (actor.getRole() == role) {
                return;
            }
        }
        throw new AccessDeniedException("RG-03: el participante no puede administrar información fuera de su rol");
    }
}
