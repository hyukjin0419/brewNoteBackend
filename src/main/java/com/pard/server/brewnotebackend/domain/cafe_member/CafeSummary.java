package com.pard.server.brewnotebackend.domain.cafe_member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CafeSummary {
    private String cafeId;
    private String CafeName;

    public static CafeSummary from(
            String cafeId,
            String cafeName
    ) {
        return CafeSummary.builder()
                .cafeId(cafeId)
                .CafeName(cafeName)
                .build();
    }
}