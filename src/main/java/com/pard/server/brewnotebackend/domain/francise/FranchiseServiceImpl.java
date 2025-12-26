package com.pard.server.brewnotebackend.domain.francise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FranchiseServiceImpl implements FranchiseService{

    private final FranchiseRepository franchiseRepository;

    @Override
    public List<FranchiseResponse> getAllFranchise() {

        List<Franchise> franchiseList = franchiseRepository.findAll();

        return franchiseList.stream().map(FranchiseResponse::fromEntity).toList();
    }
}
