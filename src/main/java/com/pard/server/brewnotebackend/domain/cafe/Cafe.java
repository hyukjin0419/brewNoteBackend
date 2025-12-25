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

    //운영
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CafeStatus status = CafeStatus.ACTIVE;

    public static Cafe of (
            UUID franchiseId,
            String name,
            String address
    ) {
        return Cafe.builder()
                .franchiseId(franchiseId)
                .name(name)
                .address(address)
                .status(CafeStatus.ACTIVE)
                .build();
    }


    // 비지니스 로직은 필요할 때 제대로 작성!
//    // --- Business Logic ---
//    public void updateInfo(String name, String address) {
//        this.name = name;
//        this.address = address;
//    }
//
//    // 폐업/삭제 처리 (Soft Delete가 적용되지만 명시적 메서드로도 제공)
//    public void closeCafe() {
//        this.status = CafeStatus.CLOSED;
//    }

}
