package com.pard.server.brewnotebackend.domain.recipe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecipeCategory {

    COFFEE("커피"),
    COLD_BREW("콜드브루"),
    NON_COFFEE("논커피"), // 라떼류 (초코라떼, 녹차라떼 등)
    ADE("에이드"),
    CLEANSE_JUICE("클렌즈 주스"),
    FRUIT_JUICE("과일 주스"),
    SHAKE("쉐이크"),
    FLATCCINO("플랫치노"), // 혹은 '블렌디드/프라페'
    TEA("티"),
    SEASONAL_MENU("시즌 메뉴"),
    SOFT_ICE_CREAM("소프트 아이스크림"),
    BREAD("브레드/베이커리");

    private final String description; // 화면에 보여줄 한글 이름
}