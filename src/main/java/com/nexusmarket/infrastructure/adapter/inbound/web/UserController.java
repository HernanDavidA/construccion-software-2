package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.command.RegisterStaff;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.UserStatus;
import com.nexusmarket.domain.port.inbound.ChangeUserStatusUseCase;
import com.nexusmarket.domain.port.inbound.QueryUsersUseCase;
import com.nexusmarket.domain.port.inbound.RegisterStaffUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final RegisterStaffUseCase registerStaff;
    private final ChangeUserStatusUseCase changeUserStatus;
    private final QueryUsersUseCase queryUsers;
    private final CurrentUser currentUser;

    public UserController(
            RegisterStaffUseCase registerStaff,
            ChangeUserStatusUseCase changeUserStatus,
            QueryUsersUseCase queryUsers,
            CurrentUser currentUser) {
        this.registerStaff = registerStaff;
        this.changeUserStatus = changeUserStatus;
        this.queryUsers = queryUsers;
        this.currentUser = currentUser;
    }

    @PostMapping("/staff")
    User registerStaff(HttpServletRequest request, @RequestBody RegisterStaff command) {
        return registerStaff.register(currentUser.require(request), command);
    }

    @PatchMapping("/{id}/status")
    User changeStatus(HttpServletRequest request, @PathVariable String id, @RequestParam UserStatus status) {
        return changeUserStatus.change(currentUser.require(request), id, status);
    }

    @GetMapping("/{id}")
    User get(HttpServletRequest request, @PathVariable String id) {
        return queryUsers.get(currentUser.require(request), id);
    }

    @GetMapping
    List<User> list(HttpServletRequest request) {
        return queryUsers.list(currentUser.require(request));
    }
}
