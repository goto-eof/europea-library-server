package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.resource.CursoredFileResource;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CursoredFileResourceImpl implements CursoredFileResource {
    final private CursoredFileSystemService cursoredFileSystemService;

    @Override
    public ResponseEntity<FileSystemItemDTO> get(@PathVariable Long fileSystemItemId) {
        return ResponseEntity.ok(cursoredFileSystemService.get(fileSystemItemId));
    }

    @Override
    public ResponseEntity<CursoredFileSystemItemDTO> retrieveCursored(@RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.readDirectory(cursorRequestDTO));
    }

    @Override
    public ResponseEntity<CursoredFileSystemItemDTO> retrieveCursoredRoot() {
        return ResponseEntity.ok(cursoredFileSystemService.readDirectory());
    }

    @Override
    public ResponseEntity<CursoredCategoryDTO> retrieveByCategoryId(@RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByCategoryId(cursorRequestDTO));
    }

    @Override
    public ResponseEntity<CursoredTagDTO> retrieveByTagId(@RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByTagId(cursorRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveByLanguage(@RequestBody GenericCursorRequestDTO<String> cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByLanguage(cursorRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveByPublishedDate(@RequestBody GenericCursorRequestDTO<String> cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByPublishedDate(cursorRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveByPublisher(@RequestBody GenericCursorRequestDTO<String> cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByPublisher(cursorRequestDTO));
    }

    @Override
    public ResponseEntity<CursoredFileExtensionDTO> retrieveCursoredByFileExtension(@RequestBody CursorTypeRequestDTO cursorTypeRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByFileExtension(cursorTypeRequestDTO));
    }

    @Override
    public ResponseEntity<List<FileExtensionDTO>> retrieveFileExtensions() {
        return ResponseEntity.ok(cursoredFileSystemService.getAllExtensions());
    }

    @Override
    public ResponseEntity<InputStreamResource> download(@PathVariable Long fileSystemItemId) {
        DownloadDTO download = this.cursoredFileSystemService.retrieveResourceForDownload(fileSystemItemId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String encodedFilename = URLEncoder.encode(download.getFileName(), StandardCharsets.UTF_8);
        headers.set("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(download.getFileSize())
                .body(download.getInputStreamResource());
    }

    @Override
    public ResponseEntity<SearchResultDTO<SearchFileSystemItemRequestDTO, FileSystemItemDTO>> search(@RequestBody SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.search(searchFileSystemItemRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveCursoredByRating(CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.readDirectoryByRating(cursorRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveCursoredByDownloadCount(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveCursoredByDownloadCount(commonCursoredRequestDTO));
    }

}
