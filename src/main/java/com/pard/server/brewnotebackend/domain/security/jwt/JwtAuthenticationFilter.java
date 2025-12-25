package com.pard.server.brewnotebackend.domain.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);

        log.info("[요청] {} {}", request.getMethod(), request.getRequestURI());
        request.getHeaderNames().asIterator()
                .forEachRemaining(header ->
                        log.info("Header: {} = {}", header, request.getHeader(header)));

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("JWT 인증 성공 - 사용자: {}", auth.getName());
        } else {
            log.info("JWT 인증 실패: 사용자가 없거나 유효하지 않음");
        }

        filterChain.doFilter(request, response);

        log.info("[응답] 상태 코드 : {}", response.getStatus());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return false;
    }

}