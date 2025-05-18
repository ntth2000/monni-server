package com.monniserver.controller;

import com.monniserver.config.JwtUtil;
import com.monniserver.dto.AuthTokenResponse;
import com.monniserver.dto.LoginRequest;
import com.monniserver.dto.RegisterRequest;
import com.monniserver.entity.User;
import com.monniserver.service.AuthService;
import com.monniserver.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        User user = authService.login(request);

        String accessToken = jwtUtil.generateJwtToken(user.getId());
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true) // cần HTTPS nếu production
                .path("/")
                .maxAge(60 * 60) // 1 hour
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok(new AuthTokenResponse(accessToken, refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("X-Refresh-Token") String refreshToken) {
        System.out.println("Token BE nhận được: " + refreshToken);

        refreshTokenService.deleteByToken(refreshToken);
        return ResponseEntity.ok("Logout successful");
    }
}
