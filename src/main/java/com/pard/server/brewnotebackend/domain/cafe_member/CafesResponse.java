package com.pard.server.brewnotebackend.domain.cafe_member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CafesResponse {

    private List<CafeSummary> cafes;

    public static CafesResponse from(
            List<CafeSummary> cafes
    ) {
        return CafesResponse.builder()
                .cafes(cafes)
                .build();
    }
}
