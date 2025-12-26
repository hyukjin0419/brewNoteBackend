package com.pard.server.brewnotebackend.domain.francise;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Franchise", description = "프렌차이즈관련 API")
@Slf4j
@RestController
@RequestMapping("api/admin/franchise")
@RequiredArgsConstructor
//TODO preauthorise ADMIN추가
public class FranchiseController {

    private final FranchiseService franchiseService;

    @GetMapping("")
    public ResponseEntity<List<FranchiseResponse>> getAllFranchise(
    ) {
        return ResponseEntity.ok(franchiseService.getAllFranchise());
    }
}
