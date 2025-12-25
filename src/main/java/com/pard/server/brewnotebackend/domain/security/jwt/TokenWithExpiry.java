package com.pard.server.brewnotebackend.domain.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TokenWithExpiry {
    private final String token;
    private final LocalDateTime expiresAt;

    public static TokenWithExpiry of(String token, LocalDateTime expiresAt) {
        return new TokenWithExpiry(token, expiresAt);
    }
}


