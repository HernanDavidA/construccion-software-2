package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.command.RegisterStaff;
import com.nexusmarket.domain.model.User;

public interface RegisterStaffUseCase {
    User register(User actor, RegisterStaff command);
}
