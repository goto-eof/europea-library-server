package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.PostDTO;

public interface PostService {

    PostDTO get(Long postId);

    PostDTO getByIdentifier(String identifier);

    PostDTO create(PostDTO postDTO);

    PostDTO update(Long id, PostDTO postDTO);

    OperationStatusDTO delete(long id);

    OperationStatusDTO deleteByIdentifier(String identifier);
}
