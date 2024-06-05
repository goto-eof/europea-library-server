package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.PaginatedExplorerOptions;
import com.andreidodu.europealibrary.model.Tag;

import java.util.List;

public interface CustomTagRepository {
    List<Tag> retrieveTagsCursored(PaginatedExplorerOptions paginatedExplorerOptions, CommonCursoredRequestDTO commonCursoredRequestDTO);
}
