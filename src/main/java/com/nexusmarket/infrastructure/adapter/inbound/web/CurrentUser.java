package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.exception.UnauthenticatedException;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.outbound.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public static final String HEADER = "X-User-Id";

    private final UserRepository users;

    public CurrentUser(UserRepository users) {
        this.users = users;
    }

    public User require(HttpServletRequest request) {
        String id = request.getHeader(HEADER);
        if (id == null || id.isBlank()) {
            throw new UnauthenticatedException("RG-01: toda operación debe ejecutarse por un usuario autenticado");
        }
        return users.findById(id.trim()).orElseThrow(() -> new UnauthenticatedException("El usuario autenticado no existe"));
    }
}
