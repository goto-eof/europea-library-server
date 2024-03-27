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
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CursorDTO<CategoryDTO> retrieveAllCategories(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        CursorDTO<CategoryDTO> cursoredResult = new CursorDTO<>();
        List<Category> categoryList = this.categoryRepository.retrieveCategoriesCursored(commonCursoredRequestDTO);
        List<CategoryDTO> tagListDTO = this.categoryMapper.toDTO(limit(categoryList));
        Long nextId = calculateNextId(categoryList);
        cursoredResult.setItems(tagListDTO);
        cursoredResult.setNextCursor(nextId);
        return cursoredResult;
    }

    private Long calculateNextId(List<Category> categoryList) {
        if (categoryList.size() <= ApplicationConst.MAX_ITEMS_RETRIEVE) {
            return null;
        }
        return categoryList.get(ApplicationConst.MAX_ITEMS_RETRIEVE).getId();
    }

    private List<Category> limit(List<Category> tagList) {
        if (tagList.size() <= ApplicationConst.MAX_ITEMS_RETRIEVE) {
            return tagList;
        }
        return tagList.stream()
                .limit(ApplicationConst.MAX_ITEMS_RETRIEVE)
                .collect(Collectors.toList());
    }
}
