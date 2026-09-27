package com.nexusmarket.application.users.port.in;

import com.nexusmarket.domain.users.Administrator;

public interface RegisterAdministratorUseCase {

    Administrator execute(String id, String identityDocument, String fullName, String email);
}
