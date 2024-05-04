package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.ItemAndFrequencyDTO;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.RenameDTO;
import com.andreidodu.europealibrary.resource.CursoredPublishedDateResource;
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
@RequiredArgsConstructor
public class CursoredPublishedDateResourceImpl implements CursoredPublishedDateResource {
    private final CursoredFileSystemServiceImpl cursoredFileSystemService;
    private final BookInfoService bookInfoService;


    @Override
    public ResponseEntity<List<ItemAndFrequencyDTO>> retrievePublishedDateCursored() {
        return ResponseEntity.ok(this.cursoredFileSystemService.retrieveAllPublishedDates());
    }

    @Override
    public ResponseEntity<OperationStatusDTO> bulkPublishedDateRename(@Valid @RequestBody RenameDTO renameDTO) {
        return ResponseEntity.ok(this.bookInfoService.bulkPublishedDateRename(renameDTO));
    }
}
