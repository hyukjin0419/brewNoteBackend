package com.pard.server.brewnotebackend.domain.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService{

    /*
    auth controller의 책임은
    - 로그인
    - 토큰 발급
    - 토큰 재발급
    - 비밀번호 변경
    - 계정 활성화
     */
}
