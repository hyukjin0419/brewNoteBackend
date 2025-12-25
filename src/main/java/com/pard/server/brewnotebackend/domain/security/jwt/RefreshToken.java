package com.pard.server.brewnotebackend.domain.security.jwt;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false,unique = true, length = 191)
    private String token;

    @Column(nullable = false)
    private boolean expired = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;


    @Builder
    public RefreshToken(Long memberId, String token, LocalDateTime expiresAt){
        this.memberId = memberId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist //엔티티를 영속 상태로 만들기 직전에 호출
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateToken(String newToken, LocalDateTime newExpiry) {
        this.token = newToken;
        this.expiresAt = newExpiry;
        this.expired = false;
        this.createdAt = LocalDateTime.now();
    }
}
