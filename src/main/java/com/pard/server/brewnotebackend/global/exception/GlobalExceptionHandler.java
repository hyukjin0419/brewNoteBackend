package com.pard.server.brewnotebackend.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.warn("Business Exception 발생 - code: {}, message: {}", errorCode.getCode(), errorCode.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("status", errorCode.getHttpStatus().value());
        body.put("errorCode", errorCode.getCode());
        body.put("timestamp", Instant.now().toString());

        if (e.getDetails() != null){
            body.put("details", e.getDetails());
        }

        if(errorCode.isClientVisible()) {
            body.put("message", errorCode.getMessage());
        } else {
            body.put("message", "서버 오류가 발생하였습니다.");
        }
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleUnexpectedException(Exception e) {
//        log.error("Unhandled 예외 발생", e);
//
//        Map<String, Object> body = Map.of(
//                "status", 500,
//                "errorCode", "INTERNAL_SERVER_ERROR",
//                "message", "서버에 오류가 발생했습니다.",
//                "timestamp", Instant.now().toString()
//        );
//
//        return ResponseEntity.internalServerError().body(body);
//    }

}
