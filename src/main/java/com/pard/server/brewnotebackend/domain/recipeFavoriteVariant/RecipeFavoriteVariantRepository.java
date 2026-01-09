package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeFavoriteVariantRepository extends JpaRepository<RecipeFavoriteVariant, Long> {
    boolean existsByCafeMemberIdAndRecipeVariantId(Long cafeMemberId, Long recipeVariantId);

    void deleteByCafeMemberIdAndRecipeVariantId(Long cafeMemberId, Long recipeVariantId);

    List<RecipeFavoriteVariant> findAllByCafeMemberId(Long cafeMemberId);



}
