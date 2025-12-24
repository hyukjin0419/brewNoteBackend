package com.pard.server.brewnotebackend.domain.security.currentUser;

import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CurrentUserDetails implements UserDetails {
    private final Long memberId;
    private final
}
