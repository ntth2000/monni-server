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
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        User user = authService.login(request);

        String accessToken = jwtUtil.generateJwtToken(user.getId());
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60) // 1 hour
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");

        return ResponseEntity.ok("Login successfully.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenService.deleteByToken(refreshToken);
        }

        ResponseCookie clearAccessToken = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie clearRefreshToken = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", clearAccessToken.toString())
                .header("Set-Cookie", clearRefreshToken.toString())
                .body("Logout successful");
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
        User user = refreshTokenService.getUserFromRefreshToken(refreshToken);
        String newAccessToken = jwtUtil.generateJwtToken(user.getId());

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false) // false nếu chạy localhost
                .path("/")
                .maxAge(60 * 60)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", accessTokenCookie.toString())
                .body(new AuthTokenResponse(newAccessToken, refreshToken));
    }

}
