package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.register(authRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest.getUsername(), authRequest.getPassword()));
    }

}
