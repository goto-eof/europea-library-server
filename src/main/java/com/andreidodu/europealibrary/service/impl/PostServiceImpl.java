package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.PostDTO;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.mapper.PostMapper;
import com.andreidodu.europealibrary.model.Post;
import com.andreidodu.europealibrary.repository.PostRepository;
import com.andreidodu.europealibrary.service.PostService;
import com.mysema.commons.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public PostDTO get(Long postId) {
        Assert.notNull(postId, "Invalid id");
        return this.postMapper.toDTO(this.postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found")));
    }

    @Override
    public PostDTO getByIdentifier(String identifier) {
        Assert.notNull(identifier, "Invalid identifier");
        return this.postMapper.toDTO(this.postRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found")));
    }

    @Override
    public PostDTO create(PostDTO postDTO) {
        Post post = this.postMapper.toModel(postDTO);
        postDTO = this.postMapper.toDTO(this.postRepository.save(post));
        return postDTO;
    }

    @Override
    public PostDTO update(Long id, PostDTO postDTO) {
        if (id == null || !id.equals(postDTO.getId())) {
            throw new ValidationException("Invalid id");
        }
        Post post = this.postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        this.postMapper.map(postDTO, post);
        post = this.postRepository.save(post);
        return this.postMapper.toDTO(post);
    }

    @Override
    public OperationStatusDTO delete(long postId
    ) {
        Assert.notNull(postId, "Invalid id");
        this.postRepository.delete(this.postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found")));
        return new OperationStatusDTO(true, "Entity deleted");
    }

    @Override
    public OperationStatusDTO deleteByIdentifier(String identifier) {
        Assert.notNull(identifier, "Invalid identifier");
        this.postRepository.delete(this.postRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found")));
        return new OperationStatusDTO(true, "Entity deleted");
    }
}
