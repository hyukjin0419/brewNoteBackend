package com.pard.server.brewnotebackend.domain.member;

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
public class StaffSummaryResponse {
    private String memberId;
    private String cafeId;
    private String cafeMemberId;
    private CafeMemberRoleType role;
    private CafeMemberStatus status;

    private String name;
    private String nickName;
    private String email;
    private String phoneNumber;

    public static StaffSummaryResponse from(
            String memberId,
            String cafeId,
            String cafeMemberId,
            CafeMemberRoleType role,
            CafeMemberStatus status,
            String name,
            String nickName,
            String email,
            String phoneNumber
    ) {
        return StaffSummaryResponse.builder()
                .memberId(memberId)
                .cafeId(cafeId)
                .cafeMemberId(cafeMemberId)
                .role(role)
                .status(status)
                .name(name)
                .nickName(nickName)
                .email(email)
                .phoneNumber(phoneNumber)
                .build();
    }
}
