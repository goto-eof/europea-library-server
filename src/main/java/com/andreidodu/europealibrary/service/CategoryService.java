package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CategoryDTO;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.model.Category;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryService {
    CursorDTO<CategoryDTO> retrieveAllCategories(CommonCursoredRequestDTO commonCursoredRequestDTO);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Category createCategoryEntity(String categoryName);
}
