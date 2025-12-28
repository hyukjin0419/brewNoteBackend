package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.RecipeFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RecipeSearchServiceTest {

    @Mock
    RecipeRepository recipeRepository;

    @InjectMocks
    RecipeServiceImpl recipeSearchService;

    @ParameterizedTest
    @ValueSource(strings = {"아ㅁㄹ", "ㅇ메", "아멜", "아메리ㅋ", "ㅇㅁㄹ"})
    @DisplayName("다양한 검색 패턴으로 아메리카노 검색")
    void recipe_search_method_Americano_test(String searchKeyword) {
        // given
        Recipe americano = RecipeFixture.americano(); //아메리카노 레시피 객체

        given(recipeRepository.searchCandidates(any(), any(), any()))
                .willReturn(List.of(americano));

        // when
        List<RecipeSearchResponse> result =
                recipeSearchService.search(searchKeyword);
        System.out.println("결과 사이즈 : " + result.size());
        System.out.println("결과: " + result.get(0).getTitle());

        // then
        assertThat(result)
                .extracting(RecipeSearchResponse::getTitle)
                .contains("아메리카노");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ㅋㅍㄹㄸ", "카페", "카ㅍ", "카페라ㄸ"})
    @DisplayName("다양한 검색 패턴으로 카페라떼 검색")
    void recipe_search_method_cafeLatte_test(String searchKeyword) {
        // given
        Recipe americano = RecipeFixture.latte();

        given(recipeRepository.searchCandidates(any(), any(), any()))
                .willReturn(List.of(americano));

        // when
        List<RecipeSearchResponse> result =
                recipeSearchService.search(searchKeyword);
        System.out.println("결과 사이즈 : " + result.size());
        System.out.println("결과: " + result.get(0).getTitle());

        // then
        assertThat(result)
                .extracting(RecipeSearchResponse::getTitle)
                .contains("카페라떼");
    }

    //입력 문자열을 "초성 시퀀스"로 변환
    /*
    아ㅁㄹ -> ㅇㅁㄹ
    ㅇ메 -> ㅇㅁ
    아멜 -> ㅇㅁㄹ
    아메리ㄱ -> ㅇㅁㄹㄱ*/
    @Test
    void 초성_시퀀스_변환_테스트() {
        RecipeSearchToken token = RecipeSearchToken.from("아메리");
        assertThat(token.getInitialSequence()).isEqualTo("ㅇㅁㄹ");

        token = RecipeSearchToken.from("ㅇ메");
        assertThat(token.getInitialSequence()).isEqualTo("ㅇㅁ");

        token = RecipeSearchToken.from("아ㅁㄹ");
        assertThat(token.getInitialSequence()).isEqualTo("ㅇㅁㄹ");

        token = RecipeSearchToken.from("아메리ㅋ");
        assertThat(token.getInitialSequence()).isEqualTo("ㅇㅁㄹㅋ");

        token = RecipeSearchToken.from("아멜");
        assertThat(token.getInitialSequence()).isEqualTo("ㅇㅁ");
    }

    //완성형 prefix 추출
    /*
    아ㅁㄹ -> 아
    ㅇ메 -> 메
     */
    @Test
    void prefix_추출_테스트() {
        RecipeSearchToken token = RecipeSearchToken.from("아ㅁㄹ");
        assertThat(token.getHangulPrefix()).isEqualTo("아");

        token = RecipeSearchToken.from("ㅇ메");
        assertThat(token.getHangulPrefix()).isEqualTo("메");
    }
}
