package com.pard.server.brewnotebackend.global.utils;

import com.pard.server.brewnotebackend.global.exception.BusinessException;
import com.pard.server.brewnotebackend.global.exception.ErrorCode;

import java.util.UUID;

public final class UuidUtils{

    private UuidUtils() {}

    public static UUID parse(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_UUID_FORMAT);
        }
    }
}

