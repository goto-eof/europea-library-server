package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import com.andreidodu.europealibrary.service.FileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/file")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class FileController {
    final private FileSystemService fileSystemService;
    final private CursoredFileSystemService cursoredFileSystemService;

    @GetMapping("/parentId/{id}")
    public ResponseEntity<FileSystemItemDTO> retrieveByParentDirectory(@PathVariable Long id) {
        FileSystemItemDTO dto = fileSystemService.readDirectory(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<FileSystemItemDTO> retrieveRootDirectory() {
        FileSystemItemDTO dto = fileSystemService.readDirectory();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<FileSystemItemDTO> retrieveCursored(@RequestBody CursorRequestDTO cursorRequestDTO) {
        FileSystemItemDTO dto = cursoredFileSystemService.readDirectory(cursorRequestDTO);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<FileSystemItemDTO> retrieveCursoredRoot(@RequestBody CursorRequestDTO cursorRequestDTO) {
        FileSystemItemDTO dto = cursoredFileSystemService.readDirectory();
        return ResponseEntity.ok(dto);
    }


}
