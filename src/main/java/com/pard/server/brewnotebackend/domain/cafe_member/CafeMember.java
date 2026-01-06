package com.pard.server.brewnotebackend.domain.cafe_member;

import com.pard.server.brewnotebackend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

//근무 소속 상태
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class CafeMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID cafeId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CafeMemberRoleType role; //OWNER, MEMBER, STAFF

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CafeMemberStatus status; //PENDING, ACTIVATED, LEAVE

    public static CafeMember createCafeOwner(
            UUID cafeId,
            UUID memberId
    ) {
        return CafeMember.builder()
                .cafeId(cafeId)
                .memberId(memberId)
                .role(CafeMemberRoleType.OWNER)
                //TODO 이메일 도입하면 Pending으로 바꿔줘야 함
                .status(CafeMemberStatus.ACTIVATED)
                .build();
    }

    //MVP에서 제외
    public static CafeMember createManager(
            UUID cafeId,
            UUID memberId
    ) {
        return CafeMember.builder()
                .cafeId(cafeId)
                .memberId(memberId)
                .role(CafeMemberRoleType.MANAGER)
                //TODO 이메일 도입하면 Pending으로 바꿔줘야 함
                .status(CafeMemberStatus.ACTIVATED)
                .build();
    }

    public static CafeMember createStaff(
            UUID cafeId,
            UUID memberId
    ) {
        return CafeMember.builder()
                .cafeId(cafeId)
                .memberId(memberId)
                .role(CafeMemberRoleType.STAFF)
                //TODO 이메일 도입하면 Pending으로 바꿔줘야 함
                .status(CafeMemberStatus.ACTIVATED)
                .build();
    }

    public void activateCafeMember() {
        this.status = CafeMemberStatus.ACTIVATED;
    }
}
