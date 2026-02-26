package com.example.shopapp.user.controller;

import com.example.shopapp.user.dto.CreateUserRequest;
import com.example.shopapp.user.dto.UpdateUserRequest;
import com.example.shopapp.user.dto.UserResponse;
import com.example.shopapp.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @GetMapping
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        return userService.getAll(page, size, username, email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}
