package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CategoryDTO;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorDTO;

import java.util.List;

public interface CategoryService {
    CursorDTO<CategoryDTO> retrieveAllCategories(CommonCursoredRequestDTO commonCursoredRequestDTO);
}
