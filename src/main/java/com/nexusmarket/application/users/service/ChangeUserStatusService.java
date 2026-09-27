package com.nexusmarket.application.users.service;

import com.nexusmarket.application.users.port.in.ChangeUserStatusUseCase;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.users.User;
import com.nexusmarket.domain.users.UserStatus;

import java.util.Objects;

/**
 * Activates or blocks any existing user (OBJ-01).
 */
public class ChangeUserStatusService implements ChangeUserStatusUseCase {

    private final UserRepository users;

    public ChangeUserStatusService(UserRepository users) {
        this.users = Objects.requireNonNull(users);
    }

    public User execute(String userId, UserStatus status) {
        Objects.requireNonNull(status, "status is required");
        User user = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        if (status == UserStatus.ACTIVE) {
            user.activate();
        } else if (status == UserStatus.BLOCKED) {
            user.block();
        } else {
            throw new IllegalArgumentException("unsupported user status");
        }
        users.save(user);
        return user;
    }
}
