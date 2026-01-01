package com.pard.server.brewnotebackend.global.security;

import com.pard.server.brewnotebackend.domain.recipe.RecipeCreateRequest;
import com.pard.server.brewnotebackend.global.security.jwt.login.LoginRequest;
import com.pard.server.brewnotebackend.global.security.jwt.login.LoginResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "로그인 관련 API")
@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        System.out.println("login 요청 객체 : " + request.getEmail());
        System.out.println("login 요청 객체 : " + request.getPassword());

        return ResponseEntity.ok(authService.login(request));
    }

}

/*
POST /auth/signup        // ADMIN / OWNER
POST /auth/login
POST /auth/activate      // STAFF
POST /auth/change-password

/admin/owners     → admin만 가능
/owners/staff     → owner만 가능

 */