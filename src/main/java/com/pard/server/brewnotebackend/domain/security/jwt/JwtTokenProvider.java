package com.pard.server.brewnotebackend.domain.security.jwt;

import com.pard.server.brewnotebackend.domain.member.MemberRoleType;
import com.pard.server.brewnotebackend.domain.security.currentUser.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.auth.secret}")
    private String secretKeyString;

    private Key secretKey;

    @Value("${jwt.access-token-valid-time}")
    private long accessTokenValidTime;

    @Value("${jwt.refresh-token-valid-time}")
    private long refreshTokenValidTime;

    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        log.info("Loaded JWT_SECRET: {}...", secretKeyString.substring(0, 8));
    }

    public String generateAccessToken(Long memberId, String userName, MemberRoleType role) {
        Claims claims = Jwts.claims().setSubject(memberId.toString());
        claims.put("userName", userName);
        claims.put("role",role.name());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenWithExpiry generateRefreshToken(Long memberId) {
        long now = System.currentTimeMillis();
        Date expiry = new Date(now + refreshTokenValidTime);

        String token = Jwts.builder()
                .setSubject(memberId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return TokenWithExpiry.of(token,
                expiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("ParserBuilder에서 JWT 인증 실패");
            return false;
        }
    }

    public Long getMemberId(String token) {
        String subject = Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.parseLong(subject);
    }


    public String getUserName (String token) {
        return (String) Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token)
                .getBody()
                .get("userName");
    }

    public String getRole(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public long getRemainingExpiration(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond();
    }
    //--------------------------------CustomUser 객체 사용 해야 합니다--------------------------------//
    public Authentication getAuthentication(String token) {
        Long memberId = getMemberId(token);
        String username = getUserName(token);
        String role = getRole(token);
        CustomUserDetails userDetails = CustomUserDetails.ofJwt(memberId, username, role);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
