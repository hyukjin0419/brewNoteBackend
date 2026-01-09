package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.francise.FranchiseRepository;
import com.pard.server.brewnotebackend.domain.francise.FranchiseResponse;
import com.pard.server.brewnotebackend.domain.member.MemberRepository;
import com.pard.server.brewnotebackend.domain.member.MemberRoleType;
import com.pard.server.brewnotebackend.global.exception.BusinessException;
import com.pard.server.brewnotebackend.global.exception.ErrorCode;
import com.pard.server.brewnotebackend.global.utils.GoogleDriveUtils;
import com.pard.server.brewnotebackend.global.utils.HangulUtils;
import com.pard.server.brewnotebackend.global.utils.UuidUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecipeServiceImpl implements RecipeService{

    private final MemberRepository memberRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeVariantRepository recipeVariantRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final FranchiseRepository franchiseRepository;
    private final RecipeAliasRepository recipeAliasRepository;

    @Override
    public void createRecipe(RecipeCreateRequest request) {
        UUID franchiseId = UuidUtils.parse(request.getFranchiseId());

        UUID creatorId = memberRepository.findByRole(MemberRoleType.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("ADMIN을 찾을 수 없습니다.")).getId();

        //레시피 저장 (초성 포함)
        if (recipeRepository.existsByTitleAndFranchiseId(request.getTitle().trim(), franchiseId)) {
            log.warn("이미 해당 프랜차이즈에 동일한 이름의 레시피가 존재합니다: {}", request.getTitle());
            throw new BusinessException(ErrorCode.DUPLICATED_RECIPE);
        }

        Recipe recipe = Recipe.of(
                franchiseId,
                creatorId,
                request.getTitle(),
                GoogleDriveUtils.convertToDirectLink(request.getHotImgUrl()),
                GoogleDriveUtils.convertToDirectLink(request.getIceImgUrl()),
                RecipeCategory.valueOf(request.getCategory()));

        // isNew 값 설정 (true/false 모두 처리)
        recipe.updateIsNew(request.isNew());

        recipeRepository.save(recipe);

        //recipe variant 저장
        validateVariants(request.getVariants());

        saveRecipeVariants(recipe, request.getVariants());

        //alias 저장
        saveRecipeAlias(recipe, request.getAlias());
    }

    //recipe variant 유효성 검증
    private void validateVariants(List<RecipeCreateRequest.VariantRequest> variants) {

        if (variants == null || variants.isEmpty()) {
            throw new BusinessException(ErrorCode.RECIPE_VARIANT_REQUIRED);
        }

        // 1. variantType 중복 방지
        Set<RecipeOptionType> types = new HashSet<>();
        for (RecipeCreateRequest.VariantRequest variant : variants) {
            if (!types.add(variant.getOptionType())) {
                throw new BusinessException(ErrorCode.DUPLICATED_VARIANT_TYPE);
            }
        }

        // 2. default variant는 정확히 1개
        long defaultCount = variants.stream()
                .filter(RecipeCreateRequest.VariantRequest::isDefault)
                .count();

        if (defaultCount != 1) {
            throw new BusinessException(ErrorCode.INVALID_DEFAULT_VARIANT);
        }
    }


    //그리고 이거를 작성할 수 있는 화면도 만들어줘야 한다 -> 이건 커서가
    //프론트로 넘겨줄 때 프렌차이즈 + 카테고리 선택할 수 있게 넘겨주어야 한다.
    @Override
    @Transactional(readOnly = true)
    public RecipeFormDataResponse getFormData() {

        List<RecipeEnumOptionResponse> recipeEnumOptionResponses =
                Arrays.stream(RecipeCategory.values())
                        .map(RecipeEnumOptionResponse::fromEnum)
                        .toList();

        List<FranchiseResponse> franchiseResponses =
                franchiseRepository.findAll().stream().map(FranchiseResponse::fromEntity).toList();
        return RecipeFormDataResponse.from(recipeEnumOptionResponses, franchiseResponses);
    }

    @Override
    public void updateRecipe(String recipeId, RecipeUpdateRequest request) {
        UUID recipeUUID = UuidUtils.parse(recipeId);

        //TODO 나중에 파라미터로 받와야하 함!, @CurrentUser 사용하기:)
        UUID creatorId = memberRepository.findByRole(MemberRoleType.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("ADMIN을 찾을 수 없습니다.")).getId();

        Recipe recipe = recipeRepository.findById(recipeUUID)
                .orElseThrow(() -> new EntityNotFoundException("수정하고자 하는 레시피를 찾을 수 없습니다;"));

        if (request.getTitle() != null) {
            recipe.updateTitle(request.getTitle());
        }
        if (request.getCategory() != null) {
            recipe.updateCategory(RecipeCategory.valueOf(request.getCategory()));
        }
        if (request.getHotThumbnailUrl() != null) {
            recipe.updateHotThumbnailUrl(request.getHotThumbnailUrl());
        }
        if (request.getIceThumbnailUrl() != null) {
            recipe.updateIceThumbnailUrl(request.getIceThumbnailUrl());
        }
        // isNew는 Boolean 타입이므로 null이 아닐 때만 업데이트 (false도 유효한 값)
        if (request.getIsNew() != null) {
            recipe.updateIsNew(request.getIsNew());
        }

        if (request.getVariants() != null) {
            validateVariants(request.getVariants());

            List<RecipeVariant> existingVariants = recipeVariantRepository.findByRecipeIdAndIsHiddenFalse(recipeUUID);

            List<Long> variantsIds = existingVariants.stream()
                    .map(RecipeVariant::getId)
                    .toList();

            recipeStepRepository.deleteAllByVariantIdIn(variantsIds);
            recipeVariantRepository.deleteAll(existingVariants);

            saveRecipeVariants(recipe, request.getVariants());
        }

        if (request.getAlias() != null) {
            recipeAliasRepository.deleteAllByRecipeId(recipe.getId());
            saveRecipeAlias(recipe, request.getAlias());
        }
    }

    @Override
    public void deleteRecipe(String recipeId) {
        UUID recipeUUID = UuidUtils.parse(recipeId);

        //TODO 나중에 파라미터로 받와야하 함!, @CurrentUser 사용하기:)
        UUID creatorId = memberRepository.findByRole(MemberRoleType.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("ADMIN을 찾을 수 없습니다.")).getId();

        Recipe recipe = recipeRepository.findById(recipeUUID)
                .orElseThrow(() ->
                        new EntityNotFoundException("삭제하려는 레시피를 찾을 수 없습니다.")
                );

        //Variant 조회
        List<RecipeVariant> variants =
                recipeVariantRepository.findByRecipeIdAndIsHiddenFalse(recipeUUID);

        if (!variants.isEmpty()) {
            List<Long> variantIds = variants.stream()
                    .map(RecipeVariant::getId)
                    .toList();

            //Step 삭제
            recipeStepRepository.deleteAllByVariantIdIn(variantIds);

            //Variant 삭제
            recipeVariantRepository.deleteAll(variants);
        }

        //Alias 삭제
        recipeAliasRepository.deleteAllByRecipeId(recipeUUID);

        //Recipe 삭제 (hard delete)
        recipeRepository.delete(recipe);
    }

    private void saveRecipeAlias(Recipe recipe, List<String> aliasInput) {
        if (aliasInput != null && !aliasInput.isEmpty()) {
            List<RecipeAlias> aliases = aliasInput.stream()
                    .map(alias -> RecipeAlias.of(
                            recipe.getId(),
                            alias
                    ))
                    .toList();

            recipeAliasRepository.saveAll(aliases);
        }
    }

    private void saveRecipeVariants(Recipe recipe, List<RecipeCreateRequest.VariantRequest> variants) {
        for (RecipeCreateRequest.VariantRequest variantRequest : variants) {
            List<String> steps = variantRequest.getSteps();
            if (steps == null || steps.isEmpty()) {
                throw new BusinessException(ErrorCode.RECIPE_STEP_REQUIRED);
            }

            RecipeVariant variant = RecipeVariant.of(recipe.getId(), variantRequest.getOptionType(), variantRequest.isDefault());
            recipeVariantRepository.save(variant);

            List<RecipeStep> recipeSteps = new ArrayList<>();
            for (int i = 0; i < steps.size(); i++) {
                recipeSteps.add(
                        RecipeStep.of(
                                variant.getId(),
                                i+1,
                                steps.get(i)
                        )
                );
            }

            recipeStepRepository.saveAll(recipeSteps);
        }
    }


    //--------------------------------------------------------------------------//
    //검색
    private static final int CANDIDATE_LIMIT = 30;
    private static final int RESULT_LIMIT = 10;

    @Override
    @Transactional(readOnly = true)
    public List<RecipeSearchResponse> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        RecipeSearchToken token = RecipeSearchToken.from(keyword);

        if (!token.hasInitial() && !token.hasHangulPrefix()) {
            return List.of();
        }

        String initialKeyword =
                token.allowInitialSearch()
                        ? "%" + token.getInitialSequence() + "%"
                        : null;

        String prefixKeyword =
                token.hasHangulPrefix()
                        ? token.getHangulPrefix() + "%"
                        : null;

        String containsKeyword =
                token.allowContainsSearch()
                        ? "%" + token.getHangulPrefix() + "%"
                        : null;

        List<Recipe> candidates = recipeRepository.searchCandidates(
                initialKeyword,
                prefixKeyword,
                containsKeyword,
                PageRequest.of(0, CANDIDATE_LIMIT)
        );

        if (candidates.isEmpty()) {
            return List.of();
        }

        List<UUID> recipeIds = candidates.stream()
                .map(Recipe::getId)
                .toList();

        Map<UUID, List<RecipeAlias>> aliasMap =
                recipeAliasRepository.findByRecipeIdIn(recipeIds).stream()
                        .collect(Collectors.groupingBy(RecipeAlias::getRecipeId));

        return candidates.stream()
                .map(recipe ->
                        matchAndScore(
                                recipe,
                                token,
                                aliasMap.getOrDefault(recipe.getId(), List.of())
                        )
                )
                .filter(ScoredRecipe::isMatched)
                .sorted(Comparator.comparingInt(ScoredRecipe::getScore).reversed())
                .limit(RESULT_LIMIT)
                .map(RecipeSearchResponse::from)
                .toList();
    }




    private ScoredRecipe matchAndScore(
            Recipe recipe,
            RecipeSearchToken token,
            List<RecipeAlias> aliases
    ) {
        if (recipe.getTitle() == null) {
            return ScoredRecipe.notMatched();
        }

        int score = 0;

        String raw = token.getRaw();
        String hangulPrefix = token.getHangulPrefix();
        String inputInitials = token.getInitialSequence();

        boolean titleMatched = false;
        boolean aliasMatched = false;

        String title = recipe.getTitle();

        // =========================
        // TITLE 기준
        // =========================

        // 완전 일치
        if (title.equals(raw)) {
            score += 100;
            titleMatched = true;
        }

        // prefix (가장 강함)
        if (!hangulPrefix.isEmpty() && title.contains(hangulPrefix)) {
            score += 90;
            titleMatched = true;
        }

        // contains (완성형 2글자 이상)
        if (token.allowContainsSearch() && title.contains(hangulPrefix)) {
            score += 60;
            titleMatched = true;
        }

        // =========================
        // 초성 검색
        // =========================
        if (token.allowInitialSearch()) {

            // 전체 제목 초성
            if (recipe.getTitleInitial().startsWith(inputInitials)) {
                score += 70;
                titleMatched = true;
            }

            // 두 번째 단어 이상 초성 (예: "연유콜드브루" → ㅋㄷㅂㄹ)
            String[] words = title.split("\\s+");
            if (words.length >= 2) {
                for (int i = 1; i < words.length; i++) {
                    String wordInitial =
                            HangulUtils.extractInitialSequence(words[i]);

                    if (wordInitial.contains(inputInitials)) {
                        score += 65; // 전체 초성보다 약간 낮게
                        titleMatched = true;
                        break;
                    }
                }
            }
        }

        // =========================
        // ALIAS 기준
        // =========================

        aliasMatched = aliases.stream().anyMatch(a -> {
            if (a.getAlias().equals(raw)) return true;

            if (!hangulPrefix.isEmpty()
                    && a.getAlias().contains(hangulPrefix)) {
                return true;
            }

            if (token.allowContainsSearch()
                    && a.getAlias().contains(hangulPrefix)) {
                return true;
            }

            // alias 초성
            return token.allowInitialSearch()
                    && a.getAliasInitials() != null
                    && a.getAliasInitials().contains(inputInitials);
        });

        if (aliasMatched) {
            score += 40;
        }

        // =========================
        // 생존 조건
        // =========================

        if (!titleMatched && !aliasMatched) {
            return ScoredRecipe.notMatched();
        }

        return ScoredRecipe.matched(recipe, score);
    }


    @Override
    @Transactional(readOnly = true)
    public List<RecipeDetailResponse> getRecipes(RecipeDetailRequest request) {
        log.info("REQ franchiseId={}, category={}, isNew={}",
                request.getFranchiseId(),
                request.getCategory(),
                request.getIsNew());

        // 둘 다 없음
        if (request.getCategory() == null && request.getIsNew() == null) {
            throw new IllegalArgumentException("category 또는 isNew 중 하나는 필수입니다.");
        }

        // 동시에 사용
        if (request.getCategory() != null && Boolean.TRUE.equals(request.getIsNew())) {
            throw new IllegalArgumentException("category와 isNeww 동시에 사용할 수 없습니다.");
        }

        UUID franchiseId = UuidUtils.parse(request.getFranchiseId());

        List<Recipe> recipes = List.of();

        if (request.getCategory() != null) {
            RecipeCategory category = RecipeCategory.valueOf(request.getCategory());
            recipes = recipeRepository
                    .findByFranchiseIdAndCategoryAndIsHiddenFalseOrderByTitleAsc(
                            franchiseId, category
                    );
        } else {
            recipes = recipeRepository
                .findByFranchiseIdAndIsNewTrueAndIsHiddenFalseOrderByCreatedAtDesc(
                        franchiseId
                );
        }

        if (recipes.isEmpty()) {
            return List.of();
        }

        // Recipe <- Variant 조회 및 배치
        List<UUID> recipeIds = recipes.stream()
                .map(Recipe::getId)
                .toList();

        List<RecipeVariant> variants = recipeVariantRepository.findByRecipeIdInAndIsHiddenFalse(recipeIds);

        Map<UUID, List<RecipeVariant>> variantMap = variants.stream()
                .collect(Collectors.groupingBy(RecipeVariant::getRecipeId));

        // variant <- step 배치 조회 및 배치
        List<Long> variantIds = variants.stream()
                .map(RecipeVariant::getId)
                .toList();

        Map<Long, List<RecipeStep>> stepMap =
                recipeStepRepository
                        .findByVariantIdInOrderByStepOrderAsc(variantIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                RecipeStep::getVariantId
                        ));

        // Response 조립
        return recipes.stream()
                .map(recipe -> {
                    List<RecipeVariant> recipeVariants =
                            variantMap.getOrDefault(recipe.getId(), List.of());

                    List<RecipeDetailResponse.VariantResponse> variantResponses =
                            recipeVariants.stream()
                                    .map(variant -> {
                                        List<String> steps =
                                                stepMap.getOrDefault(
                                                        variant.getId(),
                                                        List.of()
                                                ).stream()
                                                        .map(RecipeStep::getDescription)
                                                        .toList();

                                        return RecipeDetailResponse.VariantResponse.from(
                                                variant.getId(),
                                                variant.getType(),
                                                variant.isDefault(),
                                                steps
                                        );
                                    }).toList();

                    return RecipeDetailResponse.from(
                            recipe,
                            variantResponses
                    );
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeDetailResponse getRecipeDetail(UUID recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() ->
                        new EntityNotFoundException("레시피를 찾을 수 없습니다.")
                );

        // 2. Variant 전체 조회
        List<RecipeVariant> variants =
                recipeVariantRepository.findByRecipeIdAndIsHiddenFalse(recipeId);

        if (variants.isEmpty()) {
            throw new IllegalStateException("레시피에 Variant가 존재하지 않습니다.");
        }

        List<RecipeDetailResponse.VariantResponse> variantResponses =
                variants.stream().map(variant -> {
                    List<String> steps = recipeStepRepository
                            .findByVariantIdOrderByStepOrderAsc(variant.getId())
                            .stream()
                            .map(RecipeStep::getDescription)
                            .toList();

                    if (steps.isEmpty()) {
                        throw new IllegalStateException("Variant에 Step이 존재하지 않습니다.");
                    }

                    return RecipeDetailResponse.VariantResponse.from(
                            variant.getId(),
                            variant.getType(),
                            variant.isDefault(),
                            steps
                    );
                }).toList();

        List<String> alias = recipeAliasRepository.findByRecipeId(recipe.getId())
                .stream().map(RecipeAlias::getAlias).toList();

        return RecipeDetailResponse.from(
                recipe,
                variantResponses,
                alias
        );
    }
}