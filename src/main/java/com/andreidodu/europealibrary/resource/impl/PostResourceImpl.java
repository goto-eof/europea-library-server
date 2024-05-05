package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.PostDTO;
import com.andreidodu.europealibrary.resource.PostResource;
import com.andreidodu.europealibrary.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostResourceImpl implements PostResource {
    private final PostService postService;

    @Override
    public ResponseEntity<PostDTO> get(Long postId) {
        return ResponseEntity.ok(this.postService.get(postId));
    }

    @Override
    public ResponseEntity<PostDTO> getByIdentifier(String identifier) {
        return ResponseEntity.ok(this.postService.getByIdentifier(identifier));
    }

    @Override
    public ResponseEntity<PostDTO> create(@Valid PostDTO postDTO) {
        return ResponseEntity.ok(this.postService.create(postDTO));
    }

    @Override
    public ResponseEntity<PostDTO> update(Long postId, @Valid PostDTO postDTO) {
        return ResponseEntity.ok(this.postService.update(postId, postDTO));
    }
}
