package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
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

    @GetMapping("/parentId/{id}")
    public ResponseEntity<List<FileSystemItemDTO>> retrieveByParentDirectory(@PathVariable Long id) {
        List<FileSystemItemDTO> dtoList = fileSystemService.readDirectory(id);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping
    public ResponseEntity<List<FileSystemItemDTO>> retrieveRootDirectory() {
        List<FileSystemItemDTO> dtoList = fileSystemService.readDirectory(null);
        return ResponseEntity.ok(dtoList);
    }

}
