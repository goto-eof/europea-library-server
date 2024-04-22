package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.dto.TagDTO;
import com.andreidodu.europealibrary.model.Tag;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface TagService {
    CursorDTO<TagDTO> retrieveAllTags(CommonCursoredRequestDTO commonCursoredRequestDTO);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Tag loadOrCreateTagEntity(String tagName);
}
