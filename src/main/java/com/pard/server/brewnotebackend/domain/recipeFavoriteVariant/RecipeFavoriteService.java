package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;

import java.util.UUID;

public interface RecipeFavoriteService {
    boolean toggleFavorite(UUID memberId, RecipeFavoriteVariantRequest request);

    RecipeFavoriteListResponse getFavorites(UUID memberId, UUID cafeId);
}

