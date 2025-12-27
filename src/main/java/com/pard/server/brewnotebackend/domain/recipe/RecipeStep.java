package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class RecipeStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID recipeId;

    @Column(nullable = false)
    private Integer stepOrder; //순서 1,2,3....

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;


    public static RecipeStep of(
            UUID recipeId,
            int stepOrder,
            String description
    ) {
        return RecipeStep.builder()
                .recipeId(recipeId)
                .stepOrder(stepOrder)
                .description(description)
                .build();
    }
}
