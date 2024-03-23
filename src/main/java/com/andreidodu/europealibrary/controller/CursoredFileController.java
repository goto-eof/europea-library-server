package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.CursoredFileSystemItemDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import com.andreidodu.europealibrary.service.FileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/file/cursored")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CursoredFileController {
    final private FileSystemService fileSystemService;
    final private CursoredFileSystemService cursoredFileSystemService;

    @PostMapping
    public ResponseEntity<CursoredFileSystemItemDTO> retrieveCursored(@RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.readDirectory(cursorRequestDTO));
    }

}
