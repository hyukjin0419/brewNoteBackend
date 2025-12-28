package com.pard.server.brewnotebackend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATE_EMAIL("DUPLICATE_EMAIL",HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.",true),

    INVALID_UUID_FORMAT("INVALID_UUID_FORMAT", HttpStatus.BAD_REQUEST, "잘못된 ID 형식입니다.", true),

    DUPLICATED_RECIPE("DUPLICATED_RECIPE", HttpStatus.CONFLICT, "이미 해당 프랜차이즈에 동일한 이름의 레시피가 존재합니다.", true);


    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
    private final boolean clientVisible;
}
