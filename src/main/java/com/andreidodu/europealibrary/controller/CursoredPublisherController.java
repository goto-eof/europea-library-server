package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.ItemAndFrequencyDTO;
import com.andreidodu.europealibrary.service.impl.CursoredFileSystemServiceImpl;
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

    @GetMapping
    public ResponseEntity<List<ItemAndFrequencyDTO>> retrievePublishersCursored() {
        return ResponseEntity.ok(this.service.retrieveAllPublishers());
    }
}
