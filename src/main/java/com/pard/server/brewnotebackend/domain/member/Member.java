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

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
//MVP 단계에서는 LEAVE 멤버 자동 제외. -> SOFT DELETION으로 통합?
//관리자/통계 기능 생기면 제거 예정
@SQLRestriction("status != 'LEAVE'") //조회시 status == LEAVE는 제외하고 가져오기! -> 서비스단에서 따로 해야할까? 아니면 이렇게 하띾?
@SQLDelete(sql = "UPDATE member SET status = 'LEAVE' WHERE member_id = ?")
public class Member extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "member_id", columnDefinition = "BINARY(16)")
    private UUID id;

    // --- 로그인 & 개인 정보 ---
    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String name; //실명

    @Column(nullable = true)
    private String nickname;
    //TODO 사진, 전화번호 추가
    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private String profileImgUrl;

    // --- 상태 & 권한 ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRoleType role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;


    // --- Static Factory Method ---
    //password랑 nickname없이 작성*/ -> 지금은.. //TODO 이메일 도입 후 바꾸기
    public static Member of(
            String email,
            String password,
            String name,
            String nickname,
            MemberRoleType role
    ){
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .role(role)
                //TODO 로그인 도입 후 바꾸기
                .status(MemberStatus.ACTIVE)
                .build();
    };

    //--- Business Logic ---
    public void updateEmail(String email){
        this.email = email;
    }

    public void updateName(String name){
        this.name = name;
    }

    public void updatePhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void activate() {
        this.status = MemberStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = MemberStatus.INACTIVE;
    }
}
