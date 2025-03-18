package com.example.demo.service;

import com.example.demo.dto.AuthRequest;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.util.JwtUtil;
import com.example.demo.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtil.generateToken(user.getUsername());
    }

    public String register(AuthRequest authRequest) {
        if (userRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setDisplayName(authRequest.getDisplayName());
        user.setDisplayNameText(authRequest.getDisplayNameText());
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        // Convert role names to Role entities
        Set<Role> roles = authRequest.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);

        return "User registered successfully";
    }
}
