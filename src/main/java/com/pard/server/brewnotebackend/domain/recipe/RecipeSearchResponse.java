package com.pard.server.brewnotebackend.domain.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSearchResponse {
    private UUID recipeId;
    private String title;
    private RecipeCategory category;
    private String thumbnailUrl;
    private boolean isSignature;
    private boolean isNew;

    public static RecipeSearchResponse from(ScoredRecipe scoredRecipe) {
        Recipe recipe = scoredRecipe.getRecipe();

        return new RecipeSearchResponse(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getCategory(),
                recipe.getRepresentativeThumbnail(),
                recipe.isSignature(),
                recipe.isNew()
        );
    }

    public static RecipeSearchResponse from(Recipe recipe) {
        return new RecipeSearchResponse(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getCategory(),
                recipe.getRepresentativeThumbnail(),
                recipe.isSignature(),
                recipe.isNew()
        );
    }
}
