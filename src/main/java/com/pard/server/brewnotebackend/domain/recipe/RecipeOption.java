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
public class RecipeOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID recipeId;

    @Column(nullable = false, length = 20)
    private String optionValue; //SIZE, TEMP

    public static RecipeOption of(
            UUID recipeId,
            String optionValue
    ) {
        return RecipeOption.builder()
                .recipeId(recipeId)
                .optionValue(optionValue)
                .build();
    }
}
