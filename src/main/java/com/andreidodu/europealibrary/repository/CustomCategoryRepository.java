package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.model.Category;

import java.util.List;

public interface CustomCategoryRepository {
    List<Category> retrieveCategoriesCursored(CommonCursoredRequestDTO commonCursoredRequestDTO);
}
