package com.example.shopapp.user.mapper;

import com.example.shopapp.user.dto.CreateUserRequest;
import com.example.shopapp.user.dto.UpdateUserRequest;
import com.example.shopapp.user.dto.UserResponse;
import com.example.shopapp.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(CreateUserRequest request);
    UserResponse toResponse(User user);
    void updateUserFromDto(UpdateUserRequest dto, @MappingTarget User user);
}
