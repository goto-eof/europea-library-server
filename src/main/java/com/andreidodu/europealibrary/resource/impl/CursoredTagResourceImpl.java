package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.resource.CursoredTagResource;
import com.andreidodu.europealibrary.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CursoredTagResourceImpl implements CursoredTagResource {
    private final TagService tagService;

    @Override
    public ResponseEntity<CursorDTO<TagDTO>> retrieveTagsCursored(@RequestBody CommonCursoredRequestDTO commonCursoredRequestDTO) {
        return ResponseEntity.ok(this.tagService.retrieveAllTags(commonCursoredRequestDTO));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> bulkTagRename(@Valid @RequestBody RenameDTO renameDTO) {
        return ResponseEntity.ok(this.tagService.bulkRename(renameDTO));
    }

}
