package com.pard.server.brewnotebackend.domain.recipe;

import java.util.List;

public interface RecipeService {
    void createRecipe(RecipeCreateRequest request);

    RecipeFormDataResponse getFormData();

    List<RecipeSearchResponse> search(String keyword);
}
