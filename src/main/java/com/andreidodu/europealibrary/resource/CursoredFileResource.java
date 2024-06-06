package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowCalculatedAuthorities;
import com.andreidodu.europealibrary.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v2/file")
public interface CursoredFileResource {
    @GetMapping("/{fileSystemItemId}")
    ResponseEntity<FileSystemItemDTO> get(Authentication authentication, @PathVariable Long fileSystemItemId);

    @GetMapping("/fileMetaInfoId/{fileMetaInfoId}")
    ResponseEntity<FileSystemItemDTO> getByFileMetaInfoId(Authentication authentication, @PathVariable Long fileMetaInfoId);

    @PostMapping("/cursored")
    ResponseEntity<CursoredFileSystemItemDTO> retrieveCursored(Authentication authentication, @RequestBody CursorRequestDTO cursorRequestDTO);

    @GetMapping("/cursored")
    ResponseEntity<CursoredFileSystemItemDTO> retrieveCursoredRoot(Authentication authentication);

    @PostMapping("/cursored/category")
    ResponseEntity<CursoredCategoryDTO> retrieveByCategoryId(Authentication authentication, @RequestBody CursorRequestDTO cursorRequestDTO);

    @PostMapping("/cursored/tag")
    ResponseEntity<CursoredTagDTO> retrieveByTagId(Authentication authentication, @RequestBody CursorRequestDTO cursorRequestDTO);

    @PostMapping("/cursored/language")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveByLanguage(Authentication authentication, @RequestBody GenericCursorRequestDTO<String> cursorRequestDTO);

    @PostMapping("/cursored/publishedDate")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveByPublishedDate(Authentication authentication, @RequestBody GenericCursorRequestDTO<String> cursorRequestDTO);

    @PostMapping("/cursored/publisher")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveByPublisher(Authentication authentication, @RequestBody GenericCursorRequestDTO<String> cursorRequestDTO);

    @PostMapping("/cursored/extension")
    ResponseEntity<CursoredFileExtensionDTO> retrieveCursoredByFileExtension(Authentication authentication, @RequestBody CursorTypeRequestDTO cursorTypeRequestDTO);

    @GetMapping("/extension")
    ResponseEntity<List<FileExtensionDTO>> retrieveFileExtensions(Authentication authentication);

    @AllowCalculatedAuthorities
    @GetMapping(path = "/download/{resourceIdentifier}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, consumes = MediaType.ALL_VALUE)
    ResponseEntity<InputStreamResource> download(Authentication authentication, @PathVariable String resourceIdentifier);

    @PostMapping("/cursored/search")
    ResponseEntity<SearchResultDTO<SearchFileSystemItemRequestDTO, FileSystemItemDTO>> search(Authentication authentication, @RequestBody SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO);

    @PostMapping("/cursored/rating")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveCursoredByRating(Authentication authentication, @NotNull @RequestBody CursorCommonRequestDTO cursorRequestDTO);

    @PostMapping(path = "/cursored/downloadCount")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveCursoredByDownloadCount(Authentication authentication, @Valid @RequestBody CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO);

    @PostMapping(path = "/cursored/downloadCount/highlight")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO>> retrieveCursoredByDownloadCountHighlight(Authentication authentication, @Valid @RequestBody CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO);

    @PostMapping(path = "/cursored/new")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemDTO>> retrieveCursoredNew(Authentication authentication, @Valid @RequestBody CursorCommonRequestDTO commonRequestDTO);

    @PostMapping(path = "/cursored/new/highlight")
    ResponseEntity<GenericCursoredResponseDTO<String, FileSystemItemHighlightDTO>> retrieveCursoredNewHighlight(Authentication authentication, @Valid @RequestBody CursorCommonRequestDTO commonRequestDTO);

    @GetMapping(path = "/download/getLink/fileSystemItemId/{fileSystemItemId}")
    ResponseEntity<LinkInfoDTO> getLink(Authentication authentication, @PathVariable Long fileSystemItemId);


}
