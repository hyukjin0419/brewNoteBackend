package com.pard.server.brewnotebackend.domain.member;

import com.pard.server.brewnotebackend.domain.cafe_member.CafeMember;
import com.pard.server.brewnotebackend.domain.cafe_member.CafeMemberRoleType;
import com.pard.server.brewnotebackend.domain.cafe_member.CafeMemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDetailResponse {
    // Member 정보
    private String name;
    private String nickName;
    private String email;
    private String phoneNumber;

    // CafeMember 정보
    private CafeMemberRoleType role;
    private CafeMemberStatus status;

    public static StaffDetailResponse from(Member member, CafeMember cafeMember) {
        return StaffDetailResponse.builder()
                .name(member.getName())
                .nickName(member.getNickname())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .role(cafeMember.getRole())
                .status(cafeMember.getStatus())
                .build();
    }

}
