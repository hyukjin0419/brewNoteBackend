package com.pard.server.brewnotebackend.global.security;

import com.pard.server.brewnotebackend.global.security.jwt.login.LoginRequest;
import com.pard.server.brewnotebackend.global.security.jwt.login.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
