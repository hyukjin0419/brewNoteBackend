package com.pard.server.brewnotebackend.global.security;

import com.pard.server.brewnotebackend.domain.member.Member;
import com.pard.server.brewnotebackend.domain.member.MemberRepository;
import com.pard.server.brewnotebackend.global.exception.BusinessException;
import com.pard.server.brewnotebackend.global.exception.ErrorCode;
import com.pard.server.brewnotebackend.global.security.jwt.JwtTokenProvider;
import com.pard.server.brewnotebackend.global.security.jwt.login.LoginRequest;
import com.pard.server.brewnotebackend.global.security.jwt.login.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTHENTICATION_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(
                member.getId(),
                member.getRole()
        );

        return LoginResponse.from(accessToken, member.getRole());
    }

    /*
    auth controller의 책임은
    - 로그인
    - 토큰 발급
    - 토큰 재발급
    - 비밀번호 변경
    - 계정 활성화
     */
}
