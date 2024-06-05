package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.ItemAndFrequencyDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.RenameDTO;
import com.andreidodu.europealibrary.resource.CursoredPublisherResource;
import com.andreidodu.europealibrary.service.BookInfoService;
import com.andreidodu.europealibrary.service.impl.CursoredFileSystemServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CursoredPublisherResourceImpl implements CursoredPublisherResource {
    private final CursoredFileSystemServiceImpl service;
    private final BookInfoService bookInfoService;


    @Override
    public ResponseEntity<List<ItemAndFrequencyDTO>> retrievePublishersCursored(Authentication authentication) {
        return ResponseEntity.ok(this.service.retrieveAllPublishers(authentication));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> bulkPublisherRename(Authentication authentication, @Valid @RequestBody RenameDTO renameDTO) {
        return ResponseEntity.ok(this.bookInfoService.bulkPublisherRename(authentication, renameDTO));
    }
}
