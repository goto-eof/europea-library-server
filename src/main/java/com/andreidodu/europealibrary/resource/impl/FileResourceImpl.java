package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.resource.FileResource;
import com.andreidodu.europealibrary.service.FileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileResourceImpl implements FileResource {
    final private FileSystemService fileSystemService;

    @Override
    public ResponseEntity<FileSystemItemDTO> retrieveByParentDirectory(@PathVariable Long id) {
        FileSystemItemDTO dto = fileSystemService.readDirectory(id);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<FileSystemItemDTO> retrieveRootDirectory() {
        FileSystemItemDTO dto = fileSystemService.readDirectory();
        return ResponseEntity.ok(dto);
    }

}
