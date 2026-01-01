package com.pard.server.brewnotebackend.domain.recipe;

import java.util.List;
import java.util.UUID;

public interface RecipeService {
    void createRecipe(RecipeCreateRequest request);

    RecipeFormDataResponse getFormData();

    void updateRecipe(String recipeId, RecipeUpdateRequest request);

    List<RecipeSearchResponse> search(String keyword);

    List<RecipeDetailResponse> getRecipes(RecipeDetailRequest request);

    RecipeDetailResponse getRecipeDetail(UUID recipeId);
}
