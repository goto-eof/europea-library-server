package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.annotation.auth.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.ItemAndFrequencyDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.RenameDTO;
import com.andreidodu.europealibrary.service.BookInfoService;
import com.andreidodu.europealibrary.service.impl.CursoredFileSystemServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/publisher")
@RequiredArgsConstructor
public class CursoredPublisherController {
    private final CursoredFileSystemServiceImpl service;
    private final BookInfoService bookInfoService;


    @GetMapping
    public ResponseEntity<List<ItemAndFrequencyDTO>> retrievePublishersCursored() {
        return ResponseEntity.ok(this.service.retrieveAllPublishers());
    }

    @AllowOnlyAdministrator
    @PostMapping("/rename")
    public ResponseEntity<OperationStatusDTO> bulkPublisherRename(@Valid @RequestBody RenameDTO renameDTO) {
        return ResponseEntity.ok(this.bookInfoService.bulkPublisherRename(renameDTO));
    }
}
