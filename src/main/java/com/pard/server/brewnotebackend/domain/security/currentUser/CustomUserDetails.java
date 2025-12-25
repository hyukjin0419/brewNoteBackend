package com.pard.server.brewnotebackend.domain.security.currentUser;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CustomUserDetails implements UserDetails {
    private final Long memberId;
    private final String userName;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails ofLogin(
            Long memberId,
            String userName,
            String password,
            String role
    ) {
        return CustomUserDetails.builder()
                .memberId(memberId)
                .userName(userName)
                .password(password)
                .authorities(Collections.singleton(() -> "ROLE_" + role))
                .build();
    }

    public static CustomUserDetails ofJwt(
            Long memberId,
            String userName,
            String role
    ) {
        return CustomUserDetails.builder()
                .memberId(memberId)
                .userName(userName)
                .password(null)
                .authorities(Collections.singleton(() -> "ROLE_" + role))
                .build();
    }

    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return userName; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}