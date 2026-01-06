package com.pard.server.brewnotebackend.domain.member;

import com.pard.server.brewnotebackend.domain.cafe.CafeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDetailResponse {
    private UUID id;
    private String email;
    private String name;
    private String phoneNumber;
    private String status;
    //여기에 CafeMember 참고 해서 Cafe 추가 해야 할 듯?
    private List<CafeSummaryResponse> cafeSummaries;

    public static OwnerDetailResponse from(
            Member member,
            List<CafeSummaryResponse> cafeSummaries
    ) {
        return OwnerDetailResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .status(member.getStatus().name())
                .cafeSummaries(cafeSummaries)
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CafeSummaryResponse {
        private UUID cafeId;
        private String cafeName;
        private String status;
        private String cafeAddress;

        public static CafeSummaryResponse from(
                UUID cafeId,
                String cafeName,
                String status,
                String cafeAddress
        ) {
            return CafeSummaryResponse.builder()
                    .cafeId(cafeId)
                    .cafeName(cafeName)
                    .status(status)
                    .cafeAddress(cafeAddress)
                    .build();
        }
    }
}
