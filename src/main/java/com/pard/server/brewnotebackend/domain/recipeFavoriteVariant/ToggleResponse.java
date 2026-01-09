package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToggleResponse {
    private boolean isFavorite;

    public static ToggleResponse from(boolean isFavorite){
        return ToggleResponse.builder()
                .isFavorite(isFavorite)
                .build();
    }
}
