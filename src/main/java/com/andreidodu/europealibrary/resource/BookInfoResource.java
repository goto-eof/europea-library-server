package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    @PutMapping("/lock/{id}")
    ResponseEntity<OperationStatusDTO> lock(@PathVariable Long fileMetaInfoId) throws Exception;

    @AllowOnlyAdministrator
    @PutMapping("/unlock/{id}")
    ResponseEntity<OperationStatusDTO> unlock(@PathVariable Long fileMetaInfoId) throws Exception;
}
