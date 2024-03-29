package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CategoryDTO;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.mapper.CategoryMapper;
import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import com.andreidodu.europealibrary.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl extends CursoredServiceCommon implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CursorDTO<CategoryDTO> retrieveAllCategories(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        CursorDTO<CategoryDTO> cursoredResult = new CursorDTO<>();
        List<Category> categoryList = this.categoryRepository.retrieveCategoriesCursored(commonCursoredRequestDTO);
        List<CategoryDTO> categoryListDTO = this.categoryMapper.toDTO(limit(categoryList, ApplicationConst.MAX_ITEMS_RETRIEVE));
        calculateNextId(categoryList, ApplicationConst.MAX_ITEMS_RETRIEVE)
                .ifPresent(cursoredResult::setNextCursor);
        cursoredResult.setItems(categoryListDTO);
        return cursoredResult;
    }


}
