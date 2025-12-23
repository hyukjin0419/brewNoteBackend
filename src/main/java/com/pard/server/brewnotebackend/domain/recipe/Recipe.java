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
@SQLRestriction("is_hidden = false")
@SQLDelete(sql = "UPDATE recipe SET is_hidden = true WHERE recipe_id = ?")
public class Recipe extends BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "cafe_id", columnDefinition = "BINARY(16)")
    private UUID id;

    // --- 소속 (둘 중 하나만 값 존재) ---
    @Column(columnDefinition = "BINARY(16)")
    private UUID franchiseId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID cafeID;

    // --- 족보 (Deep Copy 추적) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_recipe_id")
    private Recipe originalRecipe; //내가 베낀 원본 레세피 (비어 있는 경우, 이게 원본 레시피임)

    @Column(columnDefinition = "BINARY(16)")
    private UUID createdBy; //작성자 ID

    // --- 기본 정보 ---
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeCategory category; //TODO ENUM으로 바꾸자

    private String thumbnailUrl;

    @Builder.Default
    private boolean isSignature = false;

    @Builder.Default
    private boolean isNew = false;

    @Builder.Default
    private boolean isHidden = false; //Soft Delete 용

    // --- 자식들 (Cascade) ---
    @Builder.Default
    @OneToMany(mappedBy =  "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeStep> steps = new ArrayList<>();

    // --- 연관관계 편의 메서드 ---
    public void addIngredient(RecipeIngredient ingredient) {
        this.ingredients.add(ingredient);
        ingredient.setThisRecipe(this);
    }

    public void addStep(RecipeStep step) {
        this.steps.add(step);
        step.setThisRecipe(this);
    }
}
