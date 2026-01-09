package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;

import java.util.UUID;

public interface RecipeFavoriteService {
    void addFavorite(UUID memberId, RecipeFavoriteVariantRequest.Add request);

    void removeFavorite(UUID memberId, RecipeFavoriteVariantRequest.Remove request);

    RecipeFavoriteListResponse getFavorites(UUID memberId, UUID cafeId);
}

