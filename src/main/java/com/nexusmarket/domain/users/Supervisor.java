package com.nexusmarket.domain.users;

/**
 * Inquiry and operational follow-up profile. Does not manage information outside their role (RG-03).
 */
public class Supervisor extends User {

    public Supervisor(String id, String identityDocument, String fullName,
                      String email, UserStatus status) {
        super(id, identityDocument, fullName, email, Role.SUPERVISOR, status);
    }
}
