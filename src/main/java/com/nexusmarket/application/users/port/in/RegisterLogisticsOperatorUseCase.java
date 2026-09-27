package com.nexusmarket.application.users.port.in;

import com.nexusmarket.domain.users.LogisticsOperator;

public interface RegisterLogisticsOperatorUseCase {

    LogisticsOperator execute(String id, String identityDocument, String fullName, String email);
}
