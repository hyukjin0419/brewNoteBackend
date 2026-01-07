package com.pard.server.brewnotebackend.domain.member;

import com.pard.server.brewnotebackend.domain.cafe_member.CafeMember;
import com.pard.server.brewnotebackend.global.utils.UuidUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffCreateRequest {
    private String cafeId;
    private String email;
    private String name;
    private String phoneNumber;
    private String encodedPassword;
    //nickname?

    public Member toMemberEntity(){
        return Member.of(
                email,
                encodedPassword,
                name,
                name,
                MemberRoleType.USER
        );
    }
}
