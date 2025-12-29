package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.francise.FranchiseRepository;
import com.pard.server.brewnotebackend.domain.francise.FranchiseResponse;
import com.pard.server.brewnotebackend.domain.member.MemberRepository;
import com.pard.server.brewnotebackend.domain.member.MemberRoleType;
import com.pard.server.brewnotebackend.global.exception.BusinessException;
import com.pard.server.brewnotebackend.global.exception.ErrorCode;
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
        //TODO 나중에 파라미터로 받와야하 함!, @CurrentUser 사용하기:)
        UUID creatorId = memberRepository.findByRole(MemberRoleType.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("ADMIN을 찾을 수 없습니다.")).getId();

        //레시피 저장 (초성 포함)
        if (recipeRepository.existsByTitleAndFranchiseId(request.getTitle().trim(), franchiseId)) {
            log.warn("이미 해당 프랜차이즈에 동일한 이름의 레시피가 존재합니다: {}", request.getTitle());
            throw new BusinessException(ErrorCode.DUPLICATED_RECIPE);
        }

        Recipe recipe = Recipe.of(franchiseId, creatorId, request.getTitle(), RecipeCategory.valueOf(request.getCategory()));

        recipeRepository.save(recipe);

        //recipe variant 저장
        validateVariants(request.getVariants());

        for (RecipeCreateRequest.VariantRequest variantRequest : request.getVariants()) {
            RecipeVariant variant = RecipeVariant.of(recipe.getId(), variantRequest.getOptionType(), variantRequest.isDefault());

            recipeVariantRepository.save(variant);

            List<String> steps = variantRequest.getSteps();
            if (steps == null || steps.isEmpty()) {
                throw new BusinessException(ErrorCode.RECIPE_STEP_REQUIRED);
            }

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

        //alias 저장
        List<String> aliasRequest = request.getAlias();
        if (aliasRequest != null && !aliasRequest.isEmpty()) {
            List<RecipeAlias> aliases = aliasRequest.stream()
                    .map(alias -> RecipeAlias.of(
                            recipe.getId(),
                            alias
                    ))
                    .toList();

            recipeAliasRepository.saveAll(aliases);
        }
    }

    //recipe variant 유효성 검증
    private void validateVariants(List<RecipeCreateRequest.VariantRequest> variants) {

        if (variants == null || variants.isEmpty()) {
            throw new BusinessException(ErrorCode.RECIPE_VARIANT_REQUIRED);
        }

        // 1. variantType 중복 방지
        Set<RecipeOptionType> types = new HashSet<>();
        for (RecipeCreateRequest.VariantRequest variant : variants) {
            System.out.println("입력된 타입 : " + variant.getOptionType());
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
    public RecipeFormDataResponse getFormData() {

        List<RecipeEnumOptionResponse> recipeEnumOptionResponses =
                Arrays.stream(RecipeCategory.values())
                        .map(RecipeEnumOptionResponse::fromEnum)
                        .toList();

        List<FranchiseResponse> franchiseResponses =
                franchiseRepository.findAll().stream().map(FranchiseResponse::fromEntity).toList();
        return RecipeFormDataResponse.from(recipeEnumOptionResponses, franchiseResponses);
    }

    //검색
    private static final int CANDIDATE_LIMIT = 30;
    private static final int RESULT_LIMIT = 10;

    @Override
    public List<RecipeSearchResponse> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) return List.of();

        RecipeSearchToken token = RecipeSearchToken.from(keyword);

        if(!token.hasInitial()) return List.of();

        List<Recipe> candidates = recipeRepository.searchCandidates(
                token.getInitialSequence() + "%",
                token.hasHangulPrefix() ? token.getHangulPrefix() + "%" : null,
                PageRequest.of(0,CANDIDATE_LIMIT)
        );

        List<UUID> recipeIds = candidates.stream()
                .map(Recipe::getId)
                .toList();

        Map<UUID, List<RecipeAlias>> aliasMap =
                recipeAliasRepository.findByRecipeIdIn(recipeIds).stream()
                        .collect(Collectors.groupingBy(RecipeAlias::getRecipeId));

        aliasMap.forEach((recipeId, aliases) -> {
            System.out.println("---------------------------------");
            System.out.println("레시피 ID: " + recipeId);
            System.out.print("등록된 별칭들: ");

            // 별칭 객체에서 이름만 추출해서 출력
            List<String> aliasNames = aliases.stream()
                    .map(RecipeAlias::getAlias) // 혹은 getName() 등 필드명에 맞게 수정
                    .toList();

            System.out.println(aliasNames);
        });

        List<ScoredRecipe> scored = candidates.stream()
                .map(recipe -> matchAndScore(recipe, token, aliasMap.getOrDefault(recipe.getId(), List.of())))
                .filter(ScoredRecipe::isMatched)
                .toList();

        return scored.stream()
                .sorted(Comparator.comparingInt(ScoredRecipe::getScore).reversed())
                .limit(RESULT_LIMIT)
                .map(RecipeSearchResponse::from)
                .toList();
    }

    private ScoredRecipe matchAndScore(Recipe recipe, RecipeSearchToken token, List<RecipeAlias> aliases) {
        if (recipe.getTitle() == null) return ScoredRecipe.notMatched();

        int score = 0;

        String raw = token.getRaw();
        String hangulPrefix = token.getHangulPrefix();
        String inputInitials = token.getInitialSequence();

        // --- title 기준 ---
        if (recipe.getTitle().equals(raw)) score += 100;

        if(!hangulPrefix.isEmpty() && recipe.getTitle().startsWith(hangulPrefix)) score += 90;

        boolean titleInitialMatched = recipe.getTitleInitial().startsWith(inputInitials);

        if(titleInitialMatched) score += 70;

        // --- alias 기준 ---
        boolean aliasMatched = aliases.stream().anyMatch(a ->
                a.getAlias().equals(raw)
                        || (!hangulPrefix.isEmpty() && a.getAlias().startsWith(hangulPrefix))
                        || (a.getAliasInitials() != null
                        && a.getAliasInitials().startsWith(inputInitials))
        );

        if (aliasMatched) score += 40; // title보다 낮게

        // --- 최종 생존 조건 ---
        if (!titleInitialMatched && !aliasMatched) {
            return ScoredRecipe.notMatched();
        }

        return ScoredRecipe.matched(recipe, score);
    }

    @Override
    public List<RecipeDetailResponse> getRecipes(RecipeDetailRequest request) {
        // 둘 다 없음
        if (request.getCategory() == null && request.getFavorite() == null) {
            throw new IllegalArgumentException("category 또는 favorite 중 하나는 필수입니다.");
        }

        // 동시에 사용
        if (request.getCategory() != null && Boolean.TRUE.equals(request.getFavorite())) {
            throw new IllegalArgumentException("category와 favorite는 동시에 사용할 수 없습니다.");
        }

        UUID franchiseId = UuidUtils.parse(request.getFranchiseId());
        RecipeCategory category = RecipeCategory.valueOf(request.getCategory());

        List<Recipe> recipes = List.of();

        if (request.getCategory() != null) {
            recipes = recipeRepository
                    .findByFranchiseIdAndCategoryAndIsHiddenFalseOrderByTitleAsc(
                            franchiseId, category
                    );
        } else {
//            recipes = recipeRepository
//                    .findByFavoriteRecipesByFranchiseIdAndMemberId(
//                            franchiseId, memberId
//                    )
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
                            recipe.getId(),
                            recipe.getTitle(),
                            recipe.getCategory().name(),
                            variantResponses
                    );
                }).toList();
    }

    @Override
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

        return RecipeDetailResponse.from(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getCategory().name(),
                variantResponses
        );
    }
}






























