package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.annotation.auth.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/v1/tag")
@RequiredArgsConstructor
public class CursoredTagController {
    private final TagService tagService;

    @PostMapping
    public ResponseEntity<CursorDTO<TagDTO>> retrieveTagsCursored(@RequestBody CommonCursoredRequestDTO commonCursoredRequestDTO) {
        return ResponseEntity.ok(this.tagService.retrieveAllTags(commonCursoredRequestDTO));
    }

    @AllowOnlyAdministrator
    @PostMapping("/rename")
    public ResponseEntity<OperationStatusDTO> bulkTagRename(@Valid @RequestBody RenameDTO renameDTO) {
        return ResponseEntity.ok(this.tagService.bulkRename(renameDTO));
    }

}
