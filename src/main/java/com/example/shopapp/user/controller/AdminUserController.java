package com.example.shopapp.user.controller;

import com.example.shopapp.user.dto.UserResponse;
import com.example.shopapp.user.entity.Role;
import com.example.shopapp.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return adminUserService.getAll(pageable);
    }

    @PatchMapping("/{id}/enable")
    public void enableUser(@PathVariable Long id, boolean enabled) {
        adminUserService.enable(id, enabled);
    }

    @PatchMapping("/{id}/role")
    public void changeRole(@PathVariable Long id,
                           @RequestParam Role role) {
        adminUserService.changeRole(id, role);
    }
}
