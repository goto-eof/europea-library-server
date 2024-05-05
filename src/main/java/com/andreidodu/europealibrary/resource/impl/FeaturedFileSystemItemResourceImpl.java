package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.GenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.resource.FeaturedFileSystemItemResource;
import com.andreidodu.europealibrary.service.FeaturedFileSystemItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeaturedFileSystemItemResourceImpl implements FeaturedFileSystemItemResource {
    private final FeaturedFileSystemItemService featuredFileSystemItemService;

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String>> retrieveCursored(CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(featuredFileSystemItemService.retrieveCursored(cursorRequestDTO));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> addFeatured(Long fileSystemItemId) {
        return ResponseEntity.ok(featuredFileSystemItemService.addFeatured(fileSystemItemId));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> removeFeatured(Long fileSystemItemId) {
        return ResponseEntity.ok(featuredFileSystemItemService.removeFeatured(fileSystemItemId));
    }
}