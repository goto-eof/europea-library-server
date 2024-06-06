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
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<FileSystemItemDTO> get(Authentication authentication, Long fileSystemItemId) {
        return ResponseEntity.ok(cursoredFileSystemService.get(authentication, fileSystemItemId));
    }

    @Override
    public ResponseEntity<FileSystemItemDTO> getByFileMetaInfoId(Authentication authentication, Long fileMetaInfoId) {
        return ResponseEntity.ok(cursoredFileSystemService.getByFileMetaInfoId(authentication, fileMetaInfoId));
    }

    @Override
    public ResponseEntity<CursoredFileSystemItemDTO> retrieveCursored(Authentication authentication, @RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.readDirectory(authentication, cursorRequestDTO));
    }

    @Override
    public ResponseEntity<CursoredFileSystemItemDTO> retrieveCursoredRoot(Authentication authentication) {
        return ResponseEntity.ok(cursoredFileSystemService.readDirectory(authentication));
    }

    @Override
    public ResponseEntity<CursoredCategoryDTO> retrieveByCategoryId(Authentication authentication, @RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByCategoryId(authentication, cursorRequestDTO));
    }

    @Override
    public ResponseEntity<CursoredTagDTO> retrieveByTagId(Authentication authentication, @RequestBody CursorRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByTagId(authentication, cursorRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveByLanguage(Authentication authentication, @RequestBody GenericCursorRequestDTO<String> cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByLanguage(authentication, cursorRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveByPublishedDate(Authentication authentication, @RequestBody GenericCursorRequestDTO<String> cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByPublishedDate(authentication, cursorRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveByPublisher(Authentication authentication, @RequestBody GenericCursorRequestDTO<String> cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByPublisher(authentication, cursorRequestDTO));
    }

    @Override
    public ResponseEntity<CursoredFileExtensionDTO> retrieveCursoredByFileExtension(Authentication authentication, @RequestBody CursorTypeRequestDTO cursorTypeRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveByFileExtension(authentication, cursorTypeRequestDTO));
    }

    @Override
    public ResponseEntity<List<FileExtensionDTO>> retrieveFileExtensions(Authentication authentication) {
        return ResponseEntity.ok(cursoredFileSystemService.getAllExtensions(authentication));
    }

    @Override
    @GetMapping(path = "/download/{resourceIdentifier}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<InputStreamResource> download(Authentication authentication, String resourceIdentifier) {
        DownloadDTO download = this.cursoredFileSystemService.retrieveResourceForDownload(authentication, resourceIdentifier);

        HttpHeaders headers = new HttpHeaders();
        String encodedFilename = URLEncoder.encode(download.getFileName(), StandardCharsets.UTF_8);
        headers.set("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(download.getFileSize())
                .body(download.getInputStreamResource());
    }

    @Override
    public ResponseEntity<SearchResultDTO<SearchFileSystemItemRequestDTO, FileSystemItemDTO>> search(Authentication authentication, @RequestBody SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.search(authentication, searchFileSystemItemRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveCursoredByRating(Authentication authentication, CursorCommonRequestDTO cursorRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveCursoredByRating(authentication, cursorRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveCursoredByDownloadCount(Authentication authentication, CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveCursoredByDownloadCount(authentication, cursoredRequestByFileTypeDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO>> retrieveCursoredByDownloadCountHighlight(Authentication authentication, CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveCursoredByDownloadCountHighlight(authentication, cursoredRequestByFileTypeDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveCursoredNew(Authentication authentication, CursorCommonRequestDTO commonRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveNewCursored(authentication, commonRequestDTO));
    }

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO>> retrieveCursoredNewHighlight(Authentication authentication, CursorCommonRequestDTO commonRequestDTO) {
        return ResponseEntity.ok(cursoredFileSystemService.retrieveNewCursoredHighlight(authentication, commonRequestDTO));
    }

    @Override
    public ResponseEntity<LinkInfoDTO> getLink(Authentication authentication, Long fileSystemItemId) {
        return ResponseEntity.ok(cursoredFileSystemService.generateDownloadLink(authentication, fileSystemItemId));
    }

}
