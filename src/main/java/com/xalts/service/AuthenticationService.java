package com.xalts.service;

import com.xalts.dto.AuthRequest;
import com.xalts.dto.AuthResponse;
import com.xalts.model.User;
import com.xalts.repository.UserRepository;
import com.xalts.security.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtService jwtService;
    @Autowired AuthenticationManager authenticationManager;

    public String register(User userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRequest.setActive(true);
        userRequest.setRole("ROLE_USER");
        User savedUser = userRepository.save(userRequest);
        String token = jwtService.generateToken(savedUser);
        return token;
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
