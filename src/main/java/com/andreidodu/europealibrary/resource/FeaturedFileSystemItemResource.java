package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/featured")
public interface FeaturedFileSystemItemResource {
    @PostMapping(path = "/cursored")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveCursored(@Valid @RequestBody CursorCommonRequestDTO cursorCommonRequestDTO);

    @PostMapping(path = "/cursored/highlight")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO>> retrieveCursoredHighlight(@Valid @RequestBody CursorCommonRequestDTO cursorCommonRequestDTO);

    @AllowOnlyAdministrator
    @GetMapping(path = "/isFeatured/{fileSystemItemId}")
    ResponseEntity<OperationStatusDTO> isFeatured(@PathVariable Long fileSystemItemId);

    @AllowOnlyAdministrator
    @PostMapping(path = "/add/{fileSystemItemId}")
    ResponseEntity<OperationStatusDTO> addFeatured(@PathVariable Long fileSystemItemId);

    @AllowOnlyAdministrator
    @PostMapping(path = "/remove/{fileSystemItemId}")
    ResponseEntity<OperationStatusDTO> removeFeatured(@PathVariable Long fileSystemItemId);

}
