package com.pard.server.brewnotebackend.domain.recipe;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class ScoredRecipe {
    private Recipe recipe;
    private int score;


    public static ScoredRecipe matched(Recipe recipe, int score) {
        return ScoredRecipe.builder()
                .recipe(recipe)
                .score(score)
                .build();
    }

    public static ScoredRecipe notMatched() {
        return new ScoredRecipe(null, 0);
    }

    public boolean isMatched() {
        return recipe != null;
    }
}
