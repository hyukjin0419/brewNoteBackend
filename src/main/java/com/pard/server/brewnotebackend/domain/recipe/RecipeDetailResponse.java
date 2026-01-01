package com.pard.server.brewnotebackend.domain.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDetailResponse {
    private UUID recipeId;
    private String title;
    private String category;
    private String hotThumbnailUrl;
    private String iceThumbnailUrl;
    private List<String> alias;

    private List<VariantResponse> variants;

    public static RecipeDetailResponse from(
            Recipe recipe,
            List<VariantResponse> variants,
            List<String> alias
    ) {
        return RecipeDetailResponse.builder()
                .recipeId(recipe.getId())
                .title(recipe.getTitle())
                .category(recipe.getCategory().name())
                .hotThumbnailUrl(recipe.getHotThumbnailUrl())
                .iceThumbnailUrl(recipe.getIceThumbnailUrl())
                .alias(alias)
                .variants(variants)
                .build();
    }

    public static RecipeDetailResponse from(
            Recipe recipe,
            List<VariantResponse> variants
    ) {
        return RecipeDetailResponse.builder()
                .recipeId(recipe.getId())
                .title(recipe.getTitle())
                .category(recipe.getCategory().name())
                .hotThumbnailUrl(recipe.getHotThumbnailUrl())
                .iceThumbnailUrl(recipe.getIceThumbnailUrl())
                .variants(variants)
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantResponse{
        private Long variantId;
        private RecipeOptionType type;
        private boolean isDefault;
        private List<String> steps;

        public static VariantResponse from(
                Long variantId,
                RecipeOptionType type,
                boolean isDefault,
                List<String> steps
        ) {
            System.out.println("여기 값 확인좀 !! : " + isDefault);
            return RecipeDetailResponse.VariantResponse.builder()
                    .variantId(variantId)
                    .type(type)
                    .isDefault(isDefault)
                    .steps(steps)
                    .build();
        }
    }
}
