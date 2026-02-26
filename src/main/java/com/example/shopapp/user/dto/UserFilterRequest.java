package com.example.shopapp.user.dto;

import lombok.Data;

@Data
public class UserFilterRequest {
    private String username;
    private String email;
}
