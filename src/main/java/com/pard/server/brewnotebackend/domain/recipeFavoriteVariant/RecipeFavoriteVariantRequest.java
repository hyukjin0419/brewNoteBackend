package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class RecipeFavoriteVariantRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Add {
        private String cafeId;
        private String recipeId;
        private Long recipeVariantId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Remove {
        private String cafeId;
        private Long recipeVariantId;
    }
}
