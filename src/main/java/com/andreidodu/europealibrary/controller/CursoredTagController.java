package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.TagDTO;
import com.andreidodu.europealibrary.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Service
@RestController
@RequestMapping("/api/v1/tag")
@CrossOrigin(origins = "${com.andreidodu.europea-library.client.url}")
@RequiredArgsConstructor
public class CursoredTagController {
    private final TagService tagService;

    @PostMapping
    public ResponseEntity<CursorDTO<TagDTO>> retrieveTagsCursored(@RequestBody CommonCursoredRequestDTO commonCursoredRequestDTO) {
        return ResponseEntity.ok(this.tagService.retrieveAllTags(commonCursoredRequestDTO));
    }
}
