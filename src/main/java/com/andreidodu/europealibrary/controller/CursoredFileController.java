package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/file/cursored")
@CrossOrigin(origins = "${com.andreidodu.europea-library.client.url}")
@RequiredArgsConstructor
public class CursoredFileController {
    final private CursoredFileSystemService cursoredFileSystemService;

    @PostMapping
    public ResponseEntity<CursoredFileSystemItemDTO> retrieveCursored(@RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.readDirectory(cursorRequestDTO));
    }

    @GetMapping
    public ResponseEntity<CursoredFileSystemItemDTO> retrieveCursoredRoot() {
        return ResponseEntity.ok(cursoredFileSystemService.readDirectory());
    }

    @PostMapping("/category")
    public ResponseEntity<CursoredCategoryDTO> retrieveByCategoryId(@RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByCategoryId(cursorRequestDTO));
    }

    @PostMapping("/tag")
    public ResponseEntity<CursoredTagDTO> retrieveByTagId(@RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByTagId(cursorRequestDTO));
    }

}
