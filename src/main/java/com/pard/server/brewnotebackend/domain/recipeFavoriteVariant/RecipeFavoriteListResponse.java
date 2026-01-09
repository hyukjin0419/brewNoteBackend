package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;

import com.pard.server.brewnotebackend.domain.recipe.RecipeOptionType;
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
public class RecipeFavoriteListResponse {

    private UUID cafeId;
    private List<Item> favorites;

    public static RecipeFavoriteListResponse from(
            UUID cafeId,
            List<Item> favorites
    ) {
        return RecipeFavoriteListResponse.builder()
                .cafeId(cafeId)
                .favorites(favorites)
                .build();
    }

    // =================================================
    // List Item (즐겨찾기 1개)
    // =================================================
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {

        private UUID recipeId;
        private String title;
        private String category;

        private String hotThumbnailUrl;
        private String iceThumbnailUrl;

        /** 즐겨찾은 variant는 1개 */
        private Variant variant;

        public static Item from(
                UUID recipeId,
                String title,
                String category,
                String hotThumbnailUrl,
                String iceThumbnailUrl,
                Variant variant
        ) {
            return Item.builder()
                    .recipeId(recipeId)
                    .title(title)
                    .category(category)
                    .hotThumbnailUrl(hotThumbnailUrl)
                    .iceThumbnailUrl(iceThumbnailUrl)
                    .variant(variant)
                    .build();
        }
    }

    // =================================================
    // Variant
    // =================================================
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Variant {

        private Long variantId;
        private RecipeOptionType type;
        private boolean isDefault;

        // 제조 step - UI 바로 사용
        private List<String> steps;

        public static Variant from(
                Long variantId,
                RecipeOptionType type,
                boolean isDefault,
                List<String> steps
        ) {
            return Variant.builder()
                    .variantId(variantId)
                    .type(type)
                    .isDefault(isDefault)
                    .steps(steps)
                    .build();
        }
    }
}
