package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.common.FileMetaInfoDTO;
import com.andreidodu.europealibrary.resource.BookInfoResource;
import com.andreidodu.europealibrary.service.BookInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookInfoResourceImpl implements BookInfoResource {
    final private BookInfoService bookInfoService;

    @Override
    public ResponseEntity<FileMetaInfoBookDTO> retrieveByFileSystemItemId(@PathVariable Long fileSystemItemId) throws Exception {
        FileMetaInfoBookDTO dto = this.bookInfoService.retrieveByFileSystemItemId(fileSystemItemId);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<FileMetaInfoBookDTO> retrieve(@PathVariable Long id) throws Exception {
        FileMetaInfoBookDTO dto = this.bookInfoService.retrieveById(id);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<FileMetaInfoBookDTO> create(@RequestBody FileMetaInfoBookDTO dto) throws Exception {
        FileMetaInfoBookDTO createdDTO = this.bookInfoService.createBookInfo(dto);
        return ResponseEntity.ok(createdDTO);
    }

//    @Override
//    public ResponseEntity<FileMetaInfoBookDTO> update(Long fileMetaInfoId, FileMetaInfoBookDTO dto) throws Exception {
//        FileMetaInfoBookDTO updatedDTO = this.bookInfoService.updateBookInfo(fileMetaInfoId, dto);
//        return ResponseEntity.ok(updatedDTO);
//    }

    @Override
    public ResponseEntity<FileMetaInfoDTO> updateByFileMetaInfo(Long fileMetaInfoId, FileMetaInfoDTO dto) {
        FileMetaInfoDTO updatedDTO = this.bookInfoService.updateFileMetaInfoFull(fileMetaInfoId, dto);
        return ResponseEntity.ok(updatedDTO);
    }

    @Override
    public ResponseEntity<OperationStatusDTO> delete(@PathVariable Long id) throws Exception {
        OperationStatusDTO operationStatusDTO = this.bookInfoService.delete(id);
        return ResponseEntity.ok(operationStatusDTO);
    }

    @Override
    public ResponseEntity<OperationStatusDTO> lock(@PathVariable Long fileMetaInfoId) throws Exception {
        return ResponseEntity.ok(this.bookInfoService.lock(fileMetaInfoId));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> unlock(@PathVariable Long fileMetaInfoId) throws Exception {
        return ResponseEntity.ok(this.bookInfoService.unlock(fileMetaInfoId));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> uploadBookCover(Long fileMetaInfoId, MultipartFile file) {
        return ResponseEntity.ok(this.bookInfoService.uploadBookCover(fileMetaInfoId, file));
    }

}
