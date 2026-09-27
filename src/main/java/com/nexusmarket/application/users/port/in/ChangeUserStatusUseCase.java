package com.nexusmarket.application.users.port.in;

import com.nexusmarket.domain.users.User;
import com.nexusmarket.domain.users.UserStatus;

public interface ChangeUserStatusUseCase {

    User execute(String userId, UserStatus status);
}
