package com.pard.server.brewnotebackend.domain.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor
public class BaseResponse {
    protected LocalDateTime createdAt;
    protected LocalDateTime modifiedAt;
}
