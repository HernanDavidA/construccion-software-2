package com.nexusmarket.application.users.port.in;

import com.nexusmarket.domain.users.Supervisor;

public interface RegisterSupervisorUseCase {

    Supervisor execute(String id, String identityDocument, String fullName, String email);
}
