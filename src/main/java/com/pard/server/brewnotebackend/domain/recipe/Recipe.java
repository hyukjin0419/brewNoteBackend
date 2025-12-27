package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
//@SQLRestriction("is_hidden = false")
//@SQLDelete(sql = "UPDATE recipe SET is_hidden = true WHERE recipe_id = ?")
public class Recipe extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "recipe_id", columnDefinition = "BINARY(16)")
    private UUID id;

    // --- 소속 (둘 중 하나만 값 존재) ---
    @Column(columnDefinition = "BINARY(16)")
    private UUID franchiseId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID cafeId;

    // --- 족보 (Deep Copy 추적) ---
    @Column(columnDefinition = "BINARY(16)")
    private UUID originalRecipeId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID createdBy; //작성자 ID

    // --- 기본 정보 ---
    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeCategory category;

    private String thumbnailUrl;

    @Builder.Default
    private boolean isSignature = false;

    @Builder.Default
    private boolean isNew = false;

    @Builder.Default
    private boolean isHidden = false; //Soft Delete 용

    public static Recipe of(
            UUID franchiseId,
            UUID creatorId,
            String title,
            RecipeCategory category
    ) {
        return Recipe.builder()
                .franchiseId(franchiseId)
                .createdBy(creatorId)
                .title(title)
                .category(category)
                .build();
    }
}
