package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;


import com.pard.server.brewnotebackend.domain.cafe_member.CafeMember;
import com.pard.server.brewnotebackend.domain.cafe_member.CafeMemberRepository;
import com.pard.server.brewnotebackend.domain.recipe.*;
import com.pard.server.brewnotebackend.global.utils.UuidUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecipeFavoriteServiceImpl implements RecipeFavoriteService{

    private final RecipeFavoriteVariantRepository favoriteRepository;
    private final CafeMemberRepository cafeMemberRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeVariantRepository recipeVariantRepository;
    private final RecipeStepRepository recipeStepRepository;

    // ===============================
    // 즐겨찾기 추가
    // ===============================
    @Override
    public boolean toggleFavorite(UUID memberId, RecipeFavoriteVariantRequest request) {

        CafeMember cafeMember = cafeMemberRepository.findByMemberIdAndCafeId(memberId, UuidUtils.parse(request.getCafeId()))
                .orElseThrow(() ->
                        new EntityNotFoundException("CafeMember not found"));

        boolean exists =
                favoriteRepository.existsByCafeMemberIdAndRecipeVariantId(
                        cafeMember.getId(),
                        request.getRecipeVariantId()
                );

        if (exists) {
            favoriteRepository.deleteByCafeMemberIdAndRecipeVariantId(
                    cafeMember.getId(),
                    request.getRecipeVariantId()
            );
            return false;
        }

        favoriteRepository.save(
                RecipeFavoriteVariant.builder()
                        .cafeMemberId(cafeMember.getId())
                        .recipeId(UuidUtils.parse(request.getRecipeId()))
                        .recipeVariantId(request.getRecipeVariantId())
                        .build()
        );
        return true;
    }

    // ===============================
    // 즐겨찾기 목록 조회 (Step 포함)
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public RecipeFavoriteListResponse getFavorites(UUID memberId, UUID cafeId) {

        // 1. cafeMember 조회
        CafeMember cafeMember =
                cafeMemberRepository.findByMemberIdAndCafeId(memberId, cafeId)
                        .orElseThrow(() ->
                                new EntityNotFoundException("CafeMember not found"));

        // 2. 즐겨찾기 목록
        List<RecipeFavoriteVariant> favorites =
                favoriteRepository.findAllByCafeMemberId(cafeMember.getId());

        if (favorites.isEmpty()) {
            return RecipeFavoriteListResponse.from(cafeId, List.of());
        }

        // 3. ID 수집
        List<UUID> recipeIds = favorites.stream()
                .map(RecipeFavoriteVariant::getRecipeId)
                .toList();

        List<Long> variantIds = favorites.stream()
                .map(RecipeFavoriteVariant::getRecipeVariantId)
                .toList();

        // 4. 배치 조회
        Map<UUID, Recipe> recipeMap =
                recipeRepository.findAllById(recipeIds).stream()
                        .collect(Collectors.toMap(Recipe::getId, r -> r));

        Map<Long, RecipeVariant> variantMap =
                recipeVariantRepository.findAllById(variantIds).stream()
                        .collect(Collectors.toMap(RecipeVariant::getId, v -> v));

        Map<Long, List<RecipeStep>> stepMap =
                recipeStepRepository
                        .findByVariantIdInOrderByStepOrderAsc(variantIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                RecipeStep::getVariantId
                        ));

        // 5. DTO 조립
        List<RecipeFavoriteListResponse.Item> items =
                favorites.stream()
                        .map(fav -> {
                            Recipe recipe = recipeMap.get(fav.getRecipeId());
                            RecipeVariant variant = variantMap.get(fav.getRecipeVariantId());

                            if (recipe == null || variant == null) {
                                return null;
                            }

                            List<String> steps =
                                    stepMap.getOrDefault(
                                                    fav.getRecipeVariantId(),
                                                    List.of()
                                            ).stream()
                                            .map(RecipeStep::getDescription)
                                            .toList();

                            return RecipeFavoriteListResponse.Item.from(
                                    recipe.getId(),
                                    recipe.getTitle(),
                                    recipe.getCategory().name(),
                                    recipe.getHotThumbnailUrl(),
                                    recipe.getIceThumbnailUrl(),
                                    RecipeFavoriteListResponse.Variant.from(
                                            variant.getId(),
                                            variant.getType(),
                                            variant.isDefault(),
                                            steps
                                    )
                            );
                        })
                        .toList();

        return RecipeFavoriteListResponse.from(cafeId, items);
    }
}
