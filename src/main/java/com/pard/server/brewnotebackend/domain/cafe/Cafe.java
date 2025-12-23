package com.pard.server.brewnotebackend.domain.cafe;

import com.pard.server.brewnotebackend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
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
@SQLRestriction("status = 'ACTIVE'")
@SQLDelete(sql = "UPDATE cafe SET status = 'CLOSED' WHERE cafe_id = ?")
public class Cafe extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "cafe_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID franchiseId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    //MVP에서는 제외...? -> 설계상 이메일 발송으로 대체할 듯?
    @Column(unique = true)
    private String accessCode; //알바생 공용 코드 -> 알바생 직접 가입시 사용

    //운영
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CafeStatus status = CafeStatus.ACTIVE;

    public static Cafe of (
            UUID franchiseId,
            String name,
            String address,
            String accessCode
    ) {
        return Cafe.builder()
                .franchiseId(franchiseId)
                .name(name)
                .address(address)
                .accessCode(accessCode)
                .status(CafeStatus.ACTIVE)
                .build();
    }

    // --- Business Logic ---
    public void updateInfo(String name, String address) {
        this.name = name;
        this.address = address;
    }

    // 간편 로그인 코드 변경
    public void updateAccessCode(String newCode) {
        this.accessCode = newCode;
    }

    // 폐업/삭제 처리 (Soft Delete가 적용되지만 명시적 메서드로도 제공)
    public void closeCafe() {
        this.status = CafeStatus.CLOSED;
    }

}
