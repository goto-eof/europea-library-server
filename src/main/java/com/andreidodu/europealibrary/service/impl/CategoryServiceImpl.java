package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.constants.CacheConst;
import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.mapper.CategoryMapper;
import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.BookInfoRepository;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.service.CategoryService;
import com.andreidodu.europealibrary.util.LimitUtil;
import com.mysema.commons.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl extends CursoredServiceCommon implements CategoryService {
    private final BookInfoRepository bookInfoRepository;
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

    private static Category createCategoryFromName(String categoryName) {
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Category createCategoryEntity(String categoryName) {
        Optional<Category> tagOptional = this.categoryRepository.findByNameIgnoreCase(categoryName);
        Category category;
        if (tagOptional.isEmpty()) {
            category = createCategoryFromName(categoryName);
            category = this.categoryRepository.save(category);
            this.categoryRepository.flush();
        } else {
            category = tagOptional.get();
        }
        return category;
    }

    @Override
    public OperationStatusDTO bulkRename(RenameDTO renameDTO) {
        Assert.notNull(renameDTO, "null payload not allowed");
        Assert.notNull(renameDTO.getNewName(), "null newName not allowed");
        Assert.notNull(renameDTO.getOldName(), "null oldName not allowed");

        if (renameDTO.getNewName().equals(renameDTO.getOldName())) {
            return new OperationStatusDTO(true, "renamed from \"" + renameDTO.getOldName() + "\" to \"" + renameDTO.getNewName() + "\"");
        }

        Optional<Category> oldTagOptional = this.categoryRepository.findByNameIgnoreCase(renameDTO.getOldName().trim().toLowerCase());
        Optional<Category> newTagOptional = this.categoryRepository.findByNameIgnoreCase(renameDTO.getNewName().trim().toLowerCase());

        Category oldCategory = oldTagOptional.orElseThrow(() -> new ValidationException("Invalid old category name"));
        Category newCategory = newTagOptional.orElseGet(() -> createCategory(renameDTO.getNewName()));

        int count = this.updateTagReference(oldCategory, newCategory);
        return new OperationStatusDTO(count > 0, "renamed from \"" + renameDTO.getOldName() + "\" to \"" + renameDTO.getNewName() + "\"");
    }


    private int updateTagReference(Category oldCategory, Category newCategory) {
        int[] count = new int[1];

        this.bookInfoRepository.retrieveFileMetaInfoContainingCategory(oldCategory)
                .forEach(bookInfoEntity -> {
                    bookInfoEntity.getCategoryList().remove(oldCategory);
                    if (!bookInfoEntity.getCategoryList().contains(newCategory)) {
                        bookInfoEntity.getCategoryList().add(newCategory);
                    }
                    this.bookInfoRepository.save(bookInfoEntity);
                    count[0]++;
                });

        this.categoryRepository.delete(oldCategory);

        return count[0];
    }


    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name.trim().toLowerCase());
        return this.categoryRepository.save(category);
    }


}
