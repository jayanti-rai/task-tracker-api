package com.jayantirai.tasktracker.auth;

import com.jayantirai.tasktracker.common.ConflictException;
import com.jayantirai.tasktracker.user.User;
import com.jayantirai.tasktracker.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder,
                       AuthenticationManager authManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest req) {
        if (users.existsByUsername(req.username())) throw new ConflictException("Username already taken");
        if (users.existsByEmail(req.email())) throw new ConflictException("Email already registered");
        User u = new User();
        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setPassword(encoder.encode(req.password()));
        users.save(u);
        return AuthResponse.bearer(jwt.generateToken(u.getUsername(), u.getRole().name()));
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        User u = users.findByUsername(req.username()).orElseThrow();
        return AuthResponse.bearer(jwt.generateToken(u.getUsername(), u.getRole().name()));
    }
}
