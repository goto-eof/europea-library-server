package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v2/file")
@RequiredArgsConstructor
public class CursoredFileController {
    final private CursoredFileSystemService cursoredFileSystemService;

    @GetMapping("/{fileSystemItemId}")
    public ResponseEntity<FileSystemItemDTO> get(@PathVariable Long fileSystemItemId) {
        return ResponseEntity.ok(cursoredFileSystemService.get(fileSystemItemId));
    }

    @PostMapping("/cursored")
    public ResponseEntity<CursoredFileSystemItemDTO> retrieveCursored(@RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.readDirectory(cursorRequestDTO));
    }

    @GetMapping("/cursored")
    public ResponseEntity<CursoredFileSystemItemDTO> retrieveCursoredRoot() {
        return ResponseEntity.ok(cursoredFileSystemService.readDirectory());
    }

    @PostMapping("/cursored/category")
    public ResponseEntity<CursoredCategoryDTO> retrieveByCategoryId(@RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByCategoryId(cursorRequestDTO));
    }

    @PostMapping("/cursored/tag")
    public ResponseEntity<CursoredTagDTO> retrieveByTagId(@RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByTagId(cursorRequestDTO));
    }


    @PostMapping("/cursored/extension")
    public ResponseEntity<CursoredFileExtensionDTO> retrieveCursoredByFileExtension(@RequestBody CursorTypeRequestDTO cursorTypeRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByFileExtension(cursorTypeRequestDTO));
    }

    @GetMapping("/extension")
    public ResponseEntity<List<FileExtensionDTO>> retrieveFileExtensions() {
        return ResponseEntity.ok(cursoredFileSystemService.getAllExtensions());
    }

    @GetMapping(path = "/download/{fileSystemItemId}")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long fileSystemItemId) {
        DownloadDTO download = this.cursoredFileSystemService.retrieveResourceForDownload(fileSystemItemId);
        return ResponseEntity.ok()
                .contentLength(download.getFileSize())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(download.getInputStreamResource());
    }

    @PostMapping("/cursored/search")
    public ResponseEntity<SearchResultDTO<SearchFileSystemItemRequestDTO, FileSystemItemDTO>> search(@RequestBody SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.search(searchFileSystemItemRequestDTO));
    }

}
