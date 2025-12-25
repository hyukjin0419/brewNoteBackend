package com.pard.server.brewnotebackend.domain.member;

import com.pard.server.brewnotebackend.domain.cafe.Cafe;
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
public class CreateOwnerRequest {
    private String email;
    private String name;
    private String phoneNumber;
    private String franchiseId;
    private String cafeName;
    private String address;

    public Member toMemberEntity(String encodedPassword){
        return Member.of(email, encodedPassword, name, name, MemberRoleType.USER);
    }

    public Cafe toCafeEntity(){
        UUID franchiseUuid = UuidUtils.parse(franchiseId);
        return Cafe.of(franchiseUuid, cafeName, address);
    }

}
