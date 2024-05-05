package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/bookInfo")
@Tag(name = "Book info resource", description = "Allows to view and manage book information")
public interface BookInfoResource {
    @GetMapping("/fileSystemItemId/{fileSystemItemId}")
    ResponseEntity<FileMetaInfoBookDTO> retrieveByFileSystemItemId(@PathVariable Long fileSystemItemId) throws Exception;

    @GetMapping("/id/{id}")
    ResponseEntity<FileMetaInfoBookDTO> retrieve(@PathVariable Long id) throws Exception;

    @AllowOnlyAdministrator
    @PostMapping("/create")
    ResponseEntity<FileMetaInfoBookDTO> create(@RequestBody FileMetaInfoBookDTO dto) throws Exception;

    @AllowOnlyAdministrator
    @PutMapping("/id/{id}")
    ResponseEntity<FileMetaInfoBookDTO> update(@PathVariable("id") Long fileMetaInfoId, @RequestBody FileMetaInfoBookDTO dto) throws Exception;

    @AllowOnlyAdministrator
    @DeleteMapping("/id/{id}")
    ResponseEntity<OperationStatusDTO> delete(@PathVariable Long id) throws Exception;

    @AllowOnlyAdministrator
    @PutMapping("/lock/{fileMetaInfoId}")
    ResponseEntity<OperationStatusDTO> lock(@PathVariable Long fileMetaInfoId) throws Exception;

    @AllowOnlyAdministrator
    @PutMapping("/unlock/{fileMetaInfoId}")
    ResponseEntity<OperationStatusDTO> unlock(@PathVariable Long fileMetaInfoId) throws Exception;

    @AllowOnlyAdministrator
    @PostMapping("/upload/cover/fileMetaInfoId/{fileMetaInfoId}")
    ResponseEntity<OperationStatusDTO> uploadBookCover(@PathVariable Long fileMetaInfoId, @RequestParam("image") MultipartFile file);


}
