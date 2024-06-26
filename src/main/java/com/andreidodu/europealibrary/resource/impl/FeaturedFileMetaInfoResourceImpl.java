package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.resource.FeaturedFileMetaInfoResource;
import com.andreidodu.europealibrary.service.ApplicationSettingsService;
import com.andreidodu.europealibrary.service.FeaturedFileSystemItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeaturedFileMetaInfoResourceImpl implements FeaturedFileMetaInfoResource {
    private final FeaturedFileSystemItemService featuredFileSystemItemService;
    private final ApplicationSettingsService applicationSettingsService;

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveCursored(Authentication authentication, CursorCommonRequestDTO cursorCommonRequestDTO) {
        return ResponseEntity.ok(featuredFileSystemItemService.retrieveCursored(authentication, cursorCommonRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO>> retrieveCursoredHighlight(Authentication authentication, CursorCommonRequestDTO cursorCommonRequestDTO) {
        return ResponseEntity.ok(featuredFileSystemItemService.retrieveCursoredHighlight(authentication, cursorCommonRequestDTO));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> isFeatured(Long fileSystemItemId) {
        return ResponseEntity.ok(featuredFileSystemItemService.isFeatured(fileSystemItemId));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> addFeatured(Long fileSystemItemId) {
        return ResponseEntity.ok(featuredFileSystemItemService.addFeatured(fileSystemItemId));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> removeFeatured(Long fileSystemItemId) {
        return ResponseEntity.ok(featuredFileSystemItemService.removeFeatured(fileSystemItemId));
    }

    @Override
    public ResponseEntity<FileSystemItemHighlightDTO> retrieveHighlight() {
        return ResponseEntity.ok(this.applicationSettingsService.getFeatured());
    }

    @Override
    public ResponseEntity<OperationStatusDTO> isHighlight(Long fileSystemItemId) {
        return ResponseEntity.ok(this.applicationSettingsService.isFeatured(fileSystemItemId));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> setHighlight(Long fileSystemItemId) {
        return ResponseEntity.ok(this.applicationSettingsService.setFeatured(fileSystemItemId));
    }

}
