package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeFavoriteVariantRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Add {
        private Long cafeMemberId;
        private UUID recipeId;
        private Long recipeVariantId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Remove {
        private Long cafeMemberId;
        private Long recipeVariantId;
    }
}
