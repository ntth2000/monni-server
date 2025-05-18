package com.monniserver.service;

import com.monniserver.entity.RefreshToken;
import com.monniserver.entity.User;
import com.monniserver.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenExpirationTime = 7 * 24 * 60 * 60 * 1000;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationTime));
        return refreshTokenRepository.save(token);
    }

    public boolean isValid(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(t -> t.getExpiryDate().isAfter(Instant.now()))
                .isPresent();
    }

    public Optional<User> getUserFromRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(RefreshToken::getUser);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
