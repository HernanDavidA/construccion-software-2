package com.nexusmarket.domain.command;

import com.nexusmarket.domain.model.Role;

public record RegisterStaff(String fullName, String email, String identityDocument, Role role) {
}
