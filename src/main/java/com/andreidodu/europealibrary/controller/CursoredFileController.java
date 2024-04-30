package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.annotation.auth.AllowOnlyAuthenticatedUsers;
import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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

    @PostMapping("/cursored/language")
    public ResponseEntity<GenericCursoredResponseDTO<String>> retrieveByLanguage(@RequestBody GenericCursorRequestDTO<String> cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByLanguage(cursorRequestDTO));
    }

    @PostMapping("/cursored/publishedDate")
    public ResponseEntity<GenericCursoredResponseDTO<String>> retrieveByPublishedDate(@RequestBody GenericCursorRequestDTO<String> cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByPublishedDate(cursorRequestDTO));
    }

    @PostMapping("/cursored/publisher")
    public ResponseEntity<GenericCursoredResponseDTO<String>> retrieveByPublisher(@RequestBody GenericCursorRequestDTO<String> cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByPublisher(cursorRequestDTO));
    }

    @PostMapping("/cursored/extension")
    public ResponseEntity<CursoredFileExtensionDTO> retrieveCursoredByFileExtension(@RequestBody CursorTypeRequestDTO cursorTypeRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByFileExtension(cursorTypeRequestDTO));
    }

    @GetMapping("/extension")
    public ResponseEntity<List<FileExtensionDTO>> retrieveFileExtensions() {
        return ResponseEntity.ok(cursoredFileSystemService.getAllExtensions());
    }

    @AllowOnlyAuthenticatedUsers
    @GetMapping(path = "/download/{fileSystemItemId}")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long fileSystemItemId) {
        DownloadDTO download = this.cursoredFileSystemService.retrieveResourceForDownload(fileSystemItemId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Content-Disposition", "attachment; filename=\"" + download.getFileName() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(download.getFileSize())
                .body(download.getInputStreamResource());
    }

    @PostMapping("/cursored/search")
    public ResponseEntity<SearchResultDTO<SearchFileSystemItemRequestDTO, FileSystemItemDTO>> search(@RequestBody SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.search(searchFileSystemItemRequestDTO));
    }

}
