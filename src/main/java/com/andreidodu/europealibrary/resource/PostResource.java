package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.dto.PostDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/post")
public interface PostResource {

    @GetMapping(path = "/id/{postId}")
    ResponseEntity<PostDTO> get(@PathVariable Long postId);

    @GetMapping(path = "/identifier/{identifier}")
    ResponseEntity<PostDTO> getByIdentifier(@PathVariable String identifier);

    @PostMapping(path = "/create")
    ResponseEntity<PostDTO> create(@RequestBody PostDTO postDTO);

    @PostMapping(path = "/update/{postId}")
    ResponseEntity<PostDTO> update(@PathVariable Long postId, @RequestBody PostDTO postDTO);
}
