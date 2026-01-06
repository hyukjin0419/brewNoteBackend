package com.pard.server.brewnotebackend.domain.member;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequest {
    private String email;
    private String name;
    private String phoneNumber;
    private String status;
}
