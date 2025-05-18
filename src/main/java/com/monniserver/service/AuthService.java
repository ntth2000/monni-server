package com.monniserver.service;

import com.monniserver.dto.LoginRequest;
import com.monniserver.dto.RegisterRequest;
import com.monniserver.entity.User;
import com.monniserver.exception.InvalidCredentialsException;
import com.monniserver.exception.UsernameAlreadyExistsException;
import com.monniserver.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {
        boolean isUserExists = userRepository.existsByUsername(request.getUsername());
        if (isUserExists) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        String username = request.getUsername();
        String password = passwordEncoder.encode(request.getPassword());
        User user = new User(username, password);

        userRepository.save(user);
    }

    public User login(LoginRequest request) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new InvalidCredentialsException("Invalid username or password")));

        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return user.get();
    }
}
