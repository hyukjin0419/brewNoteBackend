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
    private MemberRoleType role; //ADMIN, USER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status; // ACTIVE, INACTIVE

    //TODO
    /*
    - 점주가 매장 여러개 가질 수 있고
    - 스태프도 매장 여러개 넣을 수 있고
     */

    // --- Static Factory Method ---
    //계정 생성
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
                .status(MemberStatus.ACTIVE)
                .build();
    };

    //TODO 계정 생성은 여기서 확인 이후 ACTIVATION은 CafeMember에서 실행하기로 하자
    /*
    TODO
    - 계정 생성은 다 똑같이 하고
    - 계정 activate는 ONWER은 생성시에 vs. Staff는 자기가 작성해서!
     */

    // 비지니스 로직은 필요할 때 제대로 작성! --- Business Logic ---
//    public void updateInfo(String nickname){
//        this.nickname = nickname;
//    }
//
//    public void leave() {
//        this.status = MemberStatus.LEAVE;
//    }
//
//    public void vacationStart() {
//        this.status = MemberStatus.VACATION;
//    }
//
//    public void comeBack() {
//        this.status = MemberStatus.ACTIVE;
//    }
}
