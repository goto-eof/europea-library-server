package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.PostDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/post")
public interface PostResource {

    @GetMapping(path = "/id/{postId}")
    ResponseEntity<PostDTO> get(@PathVariable Long postId);

    @GetMapping(path = "/identifier/{identifier}")
    ResponseEntity<PostDTO> getByIdentifier(@PathVariable String identifier);

    @PostMapping
    @AllowOnlyAdministrator
    ResponseEntity<PostDTO> create(@RequestBody PostDTO postDTO);

    @PutMapping(path = "/postId/{postId}")
    @AllowOnlyAdministrator
    ResponseEntity<PostDTO> update(@PathVariable Long postId, @RequestBody PostDTO postDTO);
}
