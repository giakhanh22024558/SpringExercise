package com.example.demo.dto;

import lombok.Data;

import java.util.Set;

@Data
public class AuthRequest {
    private String displayName;
    private String displayNameText;
    private String username;
    private String password;
    private Set<String> roles;
}
