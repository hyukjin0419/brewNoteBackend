package com.pard.server.brewnotebackend.domain.francise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseResponse {
    UUID franchiseId;
    String name;

    public static FranchiseResponse fromEntity(Franchise franchise) {
        return FranchiseResponse.builder()
                .franchiseId(franchise.getId())
                .name(franchise.getName())
                .build();
    }
}
