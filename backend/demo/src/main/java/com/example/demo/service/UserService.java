package com.example.demo.service;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while fetching users", "details", e.getMessage()));
        }
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public ResponseEntity<?> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        try {
            // Check if username is already taken
            if (userRepository.findByUsername(userCreateRequest.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username already taken"));
            }

            // Create a new User entity
            User user = new User();
            user.setDisplayName(userCreateRequest.getDisplayName());
            user.setDisplayNameText(userCreateRequest.getDisplayNameText());
            user.setUsername(userCreateRequest.getUsername());
            user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword())); // Encrypt password

            // Convert role names to Role entities
            Set<Role> roles = userCreateRequest.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());

            user.setRoles(roles);

            // Save user to database
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "User registered successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred", "details", e.getMessage()));
        }
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setDisplayName(userDetails.getDisplayName());
            user.setDisplayNameText(userDetails.getDisplayNameText());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
