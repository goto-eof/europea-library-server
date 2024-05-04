package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/file")
public interface FileResource {
    @GetMapping("/parentId/{id}")
    ResponseEntity<FileSystemItemDTO> retrieveByParentDirectory(@PathVariable Long id);

    @GetMapping
    ResponseEntity<FileSystemItemDTO> retrieveRootDirectory();
}
