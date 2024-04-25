package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.annotation.auth.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.service.BookInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/bookInfo")
@RequiredArgsConstructor
public class BookInfoController {
    final private BookInfoService bookInfoService;

    @GetMapping("/fileSystemItemId/{fileSystemItemId}")
    public ResponseEntity<FileMetaInfoBookDTO> retrieveByFileSystemItemId(@PathVariable Long fileSystemItemId) throws Exception {
        FileMetaInfoBookDTO dto = this.bookInfoService.retrieveByFileSystemItemId(fileSystemItemId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<FileMetaInfoBookDTO> retrieve(@PathVariable Long id) throws Exception {
        FileMetaInfoBookDTO dto = this.bookInfoService.retrieveById(id);
        return ResponseEntity.ok(dto);
    }

    @AllowOnlyAdministrator
    @PostMapping("/create")
    public ResponseEntity<FileMetaInfoBookDTO> create(@RequestBody FileMetaInfoBookDTO dto) throws Exception {
        FileMetaInfoBookDTO createdDTO = this.bookInfoService.createBookInfo(dto);
        return ResponseEntity.ok(createdDTO);
    }

    @AllowOnlyAdministrator
    @PutMapping("/id/{id}")
    public ResponseEntity<FileMetaInfoBookDTO> update(@PathVariable("id") Long fileMetaInfoId, @RequestBody FileMetaInfoBookDTO dto) throws Exception {
        FileMetaInfoBookDTO updatedDTO = this.bookInfoService.updateBookInfo(fileMetaInfoId, dto);
        return ResponseEntity.ok(updatedDTO);
    }

    @AllowOnlyAdministrator
    @DeleteMapping("/id/{id}")
    public ResponseEntity<OperationStatusDTO> delete(@PathVariable Long id) throws Exception {
        OperationStatusDTO operationStatusDTO = this.bookInfoService.delete(id);
        return ResponseEntity.ok(operationStatusDTO);
    }

}
