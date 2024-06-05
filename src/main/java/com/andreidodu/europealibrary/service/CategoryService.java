package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.model.Category;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryService {
    CursorDTO<CategoryDTO> retrieveAllCategories(Authentication authentication, CommonCursoredRequestDTO commonCursoredRequestDTO);

    Category createCategoryEntity(String categoryName);

    OperationStatusDTO bulkRename(RenameDTO renameDTO);

}
