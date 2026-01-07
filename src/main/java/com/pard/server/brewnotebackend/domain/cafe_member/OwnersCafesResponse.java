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
public class OwnersCafesResponse {

    private List<OwnedCafeSummary> ownedCafes;

    public static OwnersCafesResponse from(
            List<OwnedCafeSummary> ownedCafes
    ) {
        return OwnersCafesResponse.builder()
                .ownedCafes(ownedCafes)
                .build();

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnedCafeSummary {
        private String cafeId;
        private String CafeName;

        public static OwnedCafeSummary from(
                String cafeId,
                String cafeName
        ) {
            return OwnedCafeSummary.builder()
                    .cafeId(cafeId)
                    .CafeName(cafeName)
                    .build();
        }
    }
}
