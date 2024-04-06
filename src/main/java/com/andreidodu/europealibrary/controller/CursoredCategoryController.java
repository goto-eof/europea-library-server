package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.CategoryDTO;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Service
@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CursoredCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CursorDTO<CategoryDTO>> retrieveCategories(@RequestBody CommonCursoredRequestDTO commonCursoredRequestDTO) {
        return ResponseEntity.ok(this.categoryService.retrieveAllCategories(commonCursoredRequestDTO));
    }
}
