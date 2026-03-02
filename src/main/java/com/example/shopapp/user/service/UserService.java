package com.example.shopapp.user.service;

import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.user.dto.UpdateUserRequest;
import com.example.shopapp.user.dto.UserResponse;
import com.example.shopapp.user.entity.User;
import com.example.shopapp.user.mapper.UserMapper;
import com.example.shopapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getCurrentUser(String email) {
        User user = getActiveByEmail(email);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateCurrentUser(String email, UpdateUserRequest request) {

        User user = getActiveByEmail(email);

        if (request.email() != null &&
                !request.email().equals(user.getEmail()) &&
                userRepository.existsByEmailAndDeletedFalse(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        userMapper.updateUserFromDto(request, user);

        return userMapper.toResponse(user);
    }

    private User getActiveByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }
}
