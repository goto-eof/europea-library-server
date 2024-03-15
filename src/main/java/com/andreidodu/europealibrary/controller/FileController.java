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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileController {
    private FileSystemService fileSystemService;

    @GetMapping("/parentId/{id}")
    public ResponseEntity<List<FileSystemItemDTO>> retrieveByParentDirectory(@PathVariable Long id) {
        List<FileSystemItemDTO> dtoList = fileSystemService.readDirectory(id);
        return ResponseEntity.ok(dtoList);
    }

}
