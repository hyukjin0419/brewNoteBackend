package com.pard.server.brewnotebackend.domain.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDetailRequest {
    private String franchiseId;
    private String category;
    private Boolean favorite;

    public static RecipeDetailRequest of(
            String franchiseId,
            String category,
            Boolean favorite
    ) {
        return new RecipeDetailRequest(franchiseId, category, favorite);
    }
}
