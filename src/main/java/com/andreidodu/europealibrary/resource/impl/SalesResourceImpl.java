package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.resource.SalesResource;
import com.andreidodu.europealibrary.service.SalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SalesResourceImpl implements SalesResource {
    private final SalesService salesService;

    @Override
    public ResponseEntity<CommonGenericCursoredResponseDTO<PairDTO<FileSystemItemDTO, Long>>> retrieveCursoredByTopSold(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        return ResponseEntity.ok(salesService.retrieveCursoredByTopSold(commonCursoredRequestDTO));
    }
}
