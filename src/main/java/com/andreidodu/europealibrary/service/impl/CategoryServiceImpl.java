package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.constants.CacheConst;
import com.andreidodu.europealibrary.dto.CategoryDTO;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.mapper.CategoryMapper;
import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import com.andreidodu.europealibrary.service.CategoryService;
import com.andreidodu.europealibrary.util.LimitUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl extends CursoredServiceCommon implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Cacheable(cacheNames = {CacheConst.CACHE_NAME_CATEGORIES})
    public CursorDTO<CategoryDTO> retrieveAllCategories(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        int limit = LimitUtil.calculateLimit(commonCursoredRequestDTO, ApplicationConst.CATEGORIES_MAX_ITEMS_RETRIEVE);
        CursorDTO<CategoryDTO> cursoredResult = new CursorDTO<>();
        List<Category> categoryList = this.categoryRepository.retrieveCategoriesCursored(commonCursoredRequestDTO);
        List<CategoryDTO> categoryListDTO = this.categoryMapper.toDTO(limit(categoryList, limit));
        calculateNextId(categoryList, limit)
                .ifPresent(cursoredResult::setNextCursor);
        cursoredResult.setItems(categoryListDTO);
        return cursoredResult;
    }


}
