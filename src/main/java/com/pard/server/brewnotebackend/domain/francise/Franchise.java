package com.pard.server.brewnotebackend.domain.francise;

import com.pard.server.brewnotebackend.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class Franchise extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "franchise_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String name; //ex) Ediya

    private String logoUrl;

    @Column(columnDefinition = "TEXT") //긴 설명 저장용
    private String description;

    public static Franchise of(
            String name, String logoUrl, String description
    ) {
        return Franchise.builder()
                .name(name)
                .logoUrl(logoUrl)
                .description(description)
                .build();
    }

    // --- Business Logic ---
    public void updateInfo(String name, String logoUrl, String description) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.description = description;
    }
}
