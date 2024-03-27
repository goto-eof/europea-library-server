package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.TagDTO;

public interface TagService {
    CursorDTO<TagDTO> retrieveAllTags(CommonCursoredRequestDTO commonCursoredRequestDTO);
}
