package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/featured")
public interface FeaturedFileMetaInfoResource {
    @PostMapping(path = "/cursored")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveCursored(Authentication authentication, @Valid @RequestBody CursorCommonRequestDTO cursorCommonRequestDTO);

    @PostMapping(path = "/cursored/highlight")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO>> retrieveCursoredHighlight(Authentication authentication, @Valid @RequestBody CursorCommonRequestDTO cursorCommonRequestDTO);

    @AllowOnlyAdministrator
    @GetMapping(path = "/isFeatured/{fileSystemItemId}")
    ResponseEntity<OperationStatusDTO> isFeatured(@PathVariable Long fileSystemItemId);

    @AllowOnlyAdministrator
    @PostMapping(path = "/add/{fileSystemItemId}")
    ResponseEntity<OperationStatusDTO> addFeatured(@PathVariable Long fileSystemItemId);

    @AllowOnlyAdministrator
    @PostMapping(path = "/remove/{fileSystemItemId}")
    ResponseEntity<OperationStatusDTO> removeFeatured(@PathVariable Long fileSystemItemId);

    @GetMapping(path = "/highlight")
    ResponseEntity<FileSystemItemHighlightDTO> retrieveHighlight();

    @GetMapping(path = "/isHighlight/{fileSystemItemId}")
    ResponseEntity<OperationStatusDTO> isHighlight(@PathVariable Long fileSystemItemId);

    @AllowOnlyAdministrator
    @PutMapping(path = "/highlight/{fileSystemItemId}")
    ResponseEntity<OperationStatusDTO> setHighlight(@PathVariable Long fileSystemItemId);


}
