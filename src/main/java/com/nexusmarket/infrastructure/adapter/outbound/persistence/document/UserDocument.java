package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserDocument {

    @Id
    private String id;
    private String fullName;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String identityDocument;
    private Role role;
    private UserStatus status;

    public static UserDocument from(User user) {
        return new UserDocument(user.getId(), user.getFullName(), user.getEmail(), user.getIdentityDocument(), user.getRole(), user.getStatus());
    }

    public User toDomain() {
        return new User(id, fullName, email, identityDocument, role, status);
    }
}
