package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.resource.CursoredCategoryResource;
import com.andreidodu.europealibrary.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CursoredCategoryResourceImpl implements CursoredCategoryResource {
    private final CategoryService categoryService;

    @Override
    public ResponseEntity<CursorDTO<CategoryDTO>> retrieveCategories(@RequestBody CommonCursoredRequestDTO commonCursoredRequestDTO) {
        return ResponseEntity.ok(this.categoryService.retrieveAllCategories(commonCursoredRequestDTO));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> bulkCategoryRename(@Valid @RequestBody RenameDTO renameDTO) {
        return ResponseEntity.ok(this.categoryService.bulkRename(renameDTO));
    }
}
