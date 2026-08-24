package com.nexusmarket.domain.users;

/**
 * Responsible for the physical operation of warehouses and shipments.
 */
public class LogisticsOperator extends User {

    public LogisticsOperator(String id, String identityDocument, String fullName,
                             String email, UserStatus status) {
        super(id, identityDocument, fullName, email, Role.LOGISTICS_OPERATOR, status);
    }
}
