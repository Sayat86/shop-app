package com.example.shopapp.user.service;

import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.user.dto.UserResponse;
import com.example.shopapp.user.entity.Role;
import com.example.shopapp.user.entity.User;
import com.example.shopapp.user.mapper.UserMapper;
import com.example.shopapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAllByDeletedFalse(pageable)
                .map(userMapper::toResponse);
    }

    @Transactional
    public void softDelete(Long id) {
        User user = getActiveUser(id);
        user.setDeleted(true);
    }

    @Transactional
    public void changeRole(Long id, Role role) {
        User user = getActiveUser(id);
        user.setRole(role);
    }

    @Transactional
    public void enable(Long id, boolean enabled) {
        User user = getActiveUser(id);
        user.setEnabled(enabled);
    }

    private User getActiveUser(Long id) {
        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
    }
}
