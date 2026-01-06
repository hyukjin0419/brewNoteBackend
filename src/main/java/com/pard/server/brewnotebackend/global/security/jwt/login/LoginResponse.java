package com.pard.server.brewnotebackend.global.security.jwt.login;

import com.pard.server.brewnotebackend.domain.member.MemberRoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private MemberRoleType role;

    public static LoginResponse from(String accessToken, MemberRoleType role) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .role(role)
                .build();
    }
}
