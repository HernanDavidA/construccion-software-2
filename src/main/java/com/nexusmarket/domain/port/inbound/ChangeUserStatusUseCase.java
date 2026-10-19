package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.UserStatus;

public interface ChangeUserStatusUseCase {
    User change(User actor, String userId, UserStatus status);
}
