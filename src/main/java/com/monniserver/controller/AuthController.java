package com.monniserver.controller;

import com.monniserver.config.JwtUtil;
import com.monniserver.dto.AuthTokenResponse;
import com.monniserver.dto.LoginRequest;
import com.monniserver.dto.RegisterRequest;
import com.monniserver.entity.User;
import com.monniserver.service.AuthService;
import com.monniserver.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@RequestBody LoginRequest request) {
        User user = authService.login(request);

        String accessToken = jwtUtil.generateJwtToken(user.getId());
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        return ResponseEntity.ok(new AuthTokenResponse(accessToken, refreshToken));
    }
}
