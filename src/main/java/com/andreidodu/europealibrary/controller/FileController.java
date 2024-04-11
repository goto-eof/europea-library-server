package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.service.FileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileController {
    final private FileSystemService fileSystemService;

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

}
