package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.member.MemberRepository;
import com.pard.server.brewnotebackend.domain.member.MemberRoleType;
import com.pard.server.brewnotebackend.global.utils.UuidUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecipeServiceImpl implements RecipeService{

    final private MemberRepository memberRepository;
    final private RecipeRepository recipeRepository;
    final private RecipeOptionRepository recipeOptionRepository;
    final private RecipeStepRepository recipeStepRepository;

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


        //그리고 이거를 작성할 수 있는 화면도 만들어줘야 한다 -> 이건 커서가
        //프론트로 넘겨줄 때 프렌차이즈 + 카테고리 선택할 수 있게 넘겨주어야 한다.


    }
}
