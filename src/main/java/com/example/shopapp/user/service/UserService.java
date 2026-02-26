package com.example.shopapp.user.service;

import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.exception.ResourceNotFoundException;
import com.example.shopapp.user.dto.CreateUserRequest;
import com.example.shopapp.user.dto.UpdateUserRequest;
import com.example.shopapp.user.dto.UserResponse;
import com.example.shopapp.user.entity.User;
import com.example.shopapp.user.mapper.UserMapper;
import com.example.shopapp.user.repository.UserRepository;
import com.example.shopapp.user.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);

        return userMapper.toResponse(saved);
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));

        if (request.getEmail() != null &&
                userRepository.existsByEmail(request.getEmail()) &&
                !user.getEmail().equals(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        userMapper.updateUserFromDto(request, user);
        User updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(int page, int size, String username, String email) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<User> spec = where(UserSpecification.isNotDeleted())
                .and(UserSpecification.hasUsername(username))
                .and(UserSpecification.hasEmail(email));

        Page<User> users = userRepository.findAll(spec, pageable);

        return users.map(userMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
        user.setDeleted(true);
    }
}
