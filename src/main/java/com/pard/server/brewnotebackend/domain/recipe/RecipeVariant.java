package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.common.BaseEntity;
import com.pard.server.brewnotebackend.global.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_recipe_variant_recipe_key",
                        columnNames = {"recipe_id", "variant_key"}
                )
        }
)
public class RecipeVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID recipeId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecipeOptionType type;

    @Column(nullable = false)
    private boolean isDefault;

    @Column(nullable = false)
    private boolean isHidden;

    public static RecipeVariant of(
            UUID recipeId,
            RecipeOptionType type,
            boolean isDefault
    ) {
        return RecipeVariant.builder()
                .recipeId(recipeId)
                .type(type)
                .isDefault(isDefault)
                .isHidden(false)
                .build();
    }
}
