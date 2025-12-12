package com.pard.server.brewnotebackend.domain.member;


import com.pard.server.brewnotebackend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("status != 'LEAVE'")
@SQLDelete(sql = "UPDATE member SET status = 'LEAVE' WHERE member_id = ?")
public class Member extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "member_id", columnDefinition = "BINARY(16)")
    private UUID id;

    // --- 소속 정보 (FK 객체 대신 ID 값만 저장) ---
    @Column(columnDefinition = "BINARY(16)")
    private UUID franchiseId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID cafeId;

    // --- 로그인 & 개인 정보 ---
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name; //실명

    private String nickname;

    // --- 상태 & 권한 ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role; //ADMIN, OWNER, STAFF

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status; // ACTIVE, LEAVE -> soft deletion

    // --- Static Factory Method ---
    public static Member of(
            UUID franchiseId,
            UUID cafeId,
            String username,
            String password,
            String name,
            String nickname,
            RoleType role
    ){
        return Member.builder()
                .franchiseId(franchiseId)
                .cafeId(cafeId)
                .username(username)
                .password(password)
                .name(name)
                .nickname(nickname)
                .role(role)
                .status(MemberStatus.ACTIVE)
                .build();
    };

    // --- Business Logic ---
    public void updateInfo(String nickname){
        this.nickname = nickname;
    }

    public void leave() {
        this.status = MemberStatus.LEAVE;
    }

    public void vacationStart() {
        this.status = MemberStatus.VACATION;
    }

    public void comeBack() {
        this.status = MemberStatus.ACTIVE;
    }
}
