package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAuthenticatedUsers;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.CursorTypeRequestDTO;
import com.andreidodu.europealibrary.dto.CursoredCategoryDTO;
import com.andreidodu.europealibrary.dto.CursoredFileExtensionDTO;
import com.andreidodu.europealibrary.dto.CursoredFileSystemItemDTO;
import com.andreidodu.europealibrary.dto.CursoredTagDTO;
import com.andreidodu.europealibrary.dto.FileExtensionDTO;
import com.andreidodu.europealibrary.dto.FileSystemItemDTO;
import com.andreidodu.europealibrary.dto.GenericCursorRequestDTO;
import com.andreidodu.europealibrary.dto.GenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.SearchFileSystemItemRequestDTO;
import com.andreidodu.europealibrary.dto.SearchResultDTO;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/v2/file")
public interface CursoredFileResource {
    @GetMapping("/{fileSystemItemId}")
    ResponseEntity<FileSystemItemDTO> get(@PathVariable Long fileSystemItemId);

    @PostMapping("/cursored")
    ResponseEntity<CursoredFileSystemItemDTO> retrieveCursored(@RequestBody CursorRequestDTO cursorRequestDTO);

    @GetMapping("/cursored")
    ResponseEntity<CursoredFileSystemItemDTO> retrieveCursoredRoot();

    @PostMapping("/cursored/category")
    ResponseEntity<CursoredCategoryDTO> retrieveByCategoryId(@RequestBody CursorRequestDTO cursorRequestDTO);

    @PostMapping("/cursored/tag")
    ResponseEntity<CursoredTagDTO> retrieveByTagId(@RequestBody CursorRequestDTO cursorRequestDTO);

    @PostMapping("/cursored/language")
    ResponseEntity<GenericCursoredResponseDTO<String>> retrieveByLanguage(@RequestBody GenericCursorRequestDTO<String> cursorRequestDTO);

    @PostMapping("/cursored/publishedDate")
    ResponseEntity<GenericCursoredResponseDTO<String>> retrieveByPublishedDate(@RequestBody GenericCursorRequestDTO<String> cursorRequestDTO);

    @PostMapping("/cursored/publisher")
    ResponseEntity<GenericCursoredResponseDTO<String>> retrieveByPublisher(@RequestBody GenericCursorRequestDTO<String> cursorRequestDTO);

    @PostMapping("/cursored/extension")
    ResponseEntity<CursoredFileExtensionDTO> retrieveCursoredByFileExtension(@RequestBody CursorTypeRequestDTO cursorTypeRequestDTO);

    @GetMapping("/extension")
    ResponseEntity<List<FileExtensionDTO>> retrieveFileExtensions();

    @AllowOnlyAuthenticatedUsers
    @GetMapping(path = "/download/{fileSystemItemId}")
    ResponseEntity<InputStreamResource> download(@PathVariable Long fileSystemItemId);

    @PostMapping("/cursored/search")
    ResponseEntity<SearchResultDTO<SearchFileSystemItemRequestDTO, FileSystemItemDTO>> search(@RequestBody SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO);

    @PostMapping("/cursored/rating")
    ResponseEntity<GenericCursoredResponseDTO<String>> retrieveCursoredByRating(@RequestBody CursorRequestDTO cursorRequestDTO);

    @PostMapping(path = "/cursored/downloadCount")
    ResponseEntity<GenericCursoredResponseDTO<String>> retrieveCursoredByDownloadCount(CommonCursoredRequestDTO commonCursoredRequestDTO);
}
