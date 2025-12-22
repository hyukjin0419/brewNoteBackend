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
//MVP 단계에서는 LEAVE 멤버 자동 제외.
//관리자/통계 기능 생기면 제거 예정
@SQLRestriction("status != 'LEAVE'") //조회시 status == LEAVE는 제외하고 가져오기! -> 서비스단에서 따로 해야할까? 아니면 이렇게 하띾?
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
    @Column(nullable = true, unique = true)
    private String email; //Todo 이메일로 변경

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String name; //실명

    @Column(nullable = true)
    private String nickname;

    // --- 상태 & 권한 ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role; //ADMIN, OWNER, STAFF

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status; // ACTIVE, LEAVE -> soft deletion

    // --- Static Factory Method ---
    // OWNER 계정 생성
    public static Member createActive(
            UUID franchiseId,
            UUID cafeId,
            String email,
            String password,
            String name,
            String nickname,
            RoleType role
    ){
        return Member.builder()
                .franchiseId(franchiseId)
                .cafeId(cafeId)
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .role(role)
                .status(MemberStatus.ACTIVE)
                .build();
    };

    // STAFF 계청 생성 -> 이후 초대
    public static Member inviteStaff(UUID franchiseId, UUID cafeId) {
        return Member.builder()
                .franchiseId(franchiseId)
                .cafeId(cafeId)
                .role(RoleType.STAFF)
                .status(MemberStatus.PENDING)
                .build();
    }

    //Staff 활성화 시키기
    public void activate(String email, String password, String name, String nickname) {
        if (this.status != MemberStatus.PENDING) {
            throw new IllegalStateException("이미 활성화된 계정입니다.");
        }
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.status = MemberStatus.ACTIVE;
    }


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
