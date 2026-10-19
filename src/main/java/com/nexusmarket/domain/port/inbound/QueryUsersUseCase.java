package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.User;

import java.util.List;

public interface QueryUsersUseCase {
    User get(User actor, String userId);

    List<User> list(User actor);
}
