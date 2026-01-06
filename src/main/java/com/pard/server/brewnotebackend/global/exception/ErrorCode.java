package com.pard.server.brewnotebackend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ========================
    // Member
    // ========================
    DUPLICATE_EMAIL(
            "DUPLICATE_EMAIL",
            HttpStatus.CONFLICT,
            "이미 사용 중인 이메일입니다.",
            true
    ),

    INVALID_UUID_FORMAT(
            "INVALID_UUID_FORMAT",
            HttpStatus.BAD_REQUEST,
            "잘못된 ID 형식입니다.",
            true
    ),

    // ========================
    // Recipe
    // ========================
    DUPLICATED_RECIPE(
            "DUPLICATED_RECIPE",
            HttpStatus.CONFLICT,
            "이미 해당 프랜차이즈에 동일한 이름의 레시피가 존재합니다.",
            true
    ),

    RECIPE_VARIANT_REQUIRED(
            "RECIPE_VARIANT_REQUIRED",
            HttpStatus.BAD_REQUEST,
            "레시피에는 최소 하나 이상의 제조 Variant가 필요합니다.",
            true
    ),

    DUPLICATED_VARIANT_TYPE(
            "DUPLICATED_VARIANT_TYPE",
            HttpStatus.CONFLICT,
            "동일한 Variant 타입이 중복되었습니다.",
            true
    ),

    INVALID_DEFAULT_VARIANT(
            "INVALID_DEFAULT_VARIANT",
            HttpStatus.BAD_REQUEST,
            "기본 제조 Variant는 정확히 하나여야 합니다.",
            true
    ),

    RECIPE_STEP_REQUIRED(
            "RECIPE_STEP_REQUIRED",
            HttpStatus.BAD_REQUEST,
            "각 제조 Variant에는 최소 하나 이상의 제조 Step이 필요합니다.",
            true
    ),

    AUTHENTICATION_FAILED(
        "AUTHENTICATION_FAILED",
        HttpStatus.UNAUTHORIZED,
        "이메일 또는 비밀번호가 올바르지 않습니다.",
                true
    );


    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
    private final boolean clientVisible;
}