package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.francise.FranchiseRepository;
import com.pard.server.brewnotebackend.domain.francise.FranchiseResponse;
import com.pard.server.brewnotebackend.domain.member.MemberRepository;
import com.pard.server.brewnotebackend.domain.member.MemberRoleType;
import com.pard.server.brewnotebackend.global.utils.UuidUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecipeServiceImpl implements RecipeService{

    final private MemberRepository memberRepository;
    final private RecipeRepository recipeRepository;
    final private RecipeOptionRepository recipeOptionRepository;
    final private RecipeStepRepository recipeStepRepository;
    final private FranchiseRepository franchiseRepository;
    final private RecipeAliasRepository recipeAliasRepository;

    @Override
    public void createRecipe(RecipeCreateRequest request) {
        UUID franchiseId = UuidUtils.parse(request.getFranchiseId());
        //TODO 나중에 파라미터로 받와야하 함!, @CurrentUser 사용하기:)
        UUID creatorId = memberRepository.findByRole(MemberRoleType.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("ADMIN을 찾을 수 없습니다.")).getId();

        Recipe recipe = Recipe.of(franchiseId, creatorId, request.getTitle(), RecipeCategory.valueOf(request.getCategory()));

        recipeRepository.save(recipe);

        List<String> optionValues = request.getRecipeOptions();

        if (optionValues != null && !optionValues.isEmpty()) {
            List<RecipeOption> options = optionValues.stream()
                    .map(option -> RecipeOption.of(recipe.getId(), option)).toList();

            recipeOptionRepository.saveAll(options);
        }

        List<String> stepContents = request.getSteps();

        if (stepContents != null && !stepContents.isEmpty()) {
            List<RecipeStep> steps = new ArrayList<>();

            for (int i = 0; i < stepContents.size(); i++) {
                steps.add(
                        RecipeStep.of(
                                recipe.getId(),
                                i + 1,
                                stepContents.get(i)
                        )
                );
            }
            recipeStepRepository.saveAll(steps);
        }

        List<String> aliasRequest = request.getAlias();
        if (aliasRequest != null && !aliasRequest.isEmpty()) {
            List<RecipeAlias> aliases = aliasRequest.stream()
                    .map(RecipeAlias::of)
                    .toList();

            recipeAliasRepository.saveAll(aliases);
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

        List<ScoredRecipe> scored = candidates.stream()
                .map(recipe -> matchAndScore(recipe, token))
                .filter(ScoredRecipe::isMatched)
                .toList();

        return scored.stream()
                .sorted(Comparator.comparingInt(ScoredRecipe::getScore).reversed())
                .limit(RESULT_LIMIT)
                .map(RecipeSearchResponse::from)
                .toList();
    }

    private ScoredRecipe matchAndScore(Recipe recipe, RecipeSearchToken token) {

        int score = 0;

        String raw = token.getRaw();
        String hangulPrefix = token.getHangulPrefix();
        String inputInitials = token.getInitialSequence();

        if (recipe.getTitle().equals(raw)) score += 100;

        if(!hangulPrefix.isEmpty() && recipe.getTitle().startsWith(hangulPrefix)) score += 90;

        if(!recipe.getTitleInitial().startsWith(inputInitials)) return ScoredRecipe.notMatched();

        score += 70;

        return ScoredRecipe.matched(recipe, score);
    }
}






























