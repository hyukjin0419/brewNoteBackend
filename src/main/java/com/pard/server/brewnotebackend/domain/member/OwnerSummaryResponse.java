package com.pard.server.brewnotebackend.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerSummaryResponse {
    private UUID id;
    private String email;
    private String name;
    private String phoneNumber;
    private String status;
    private UUID representativeCafeId;
    private String representativeCafe;
}