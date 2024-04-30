package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.constants.CacheConst;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.service.CacheLoader;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheLoaderImpl implements CacheLoader {
    private final CursoredFileSystemService cursoredFileSystemService;
    private final CategoryServiceImpl categoryService;
    private final TagServiceImpl tagService;
    private final CacheManager cacheManager;

    @Override
    public void reload() {
        log.info("loading cache");

        reloadExtensionsInCache();
        reloadCategoriesInCache();
        reloadTagsInCache();
        reloadLanguagesInCache();
        reloadPublishersInCache();

        log.info("cache loaded");
    }

    @Override
    public void reloadExtensionsInCache() {
        Optional.ofNullable(cacheManager.getCache(CacheConst.CACHE_NAME_EXTENSIONS))
                .ifPresent(Cache::clear);
        this.cursoredFileSystemService.getAllExtensions();
    }

    @Override
    public void reloadTagsInCache() {
        Optional.ofNullable(cacheManager.getCache(CacheConst.CACHE_NAME_TAGS))
                .ifPresent(Cache::clear);
        var response = this.tagService.retrieveAllTags(new CommonCursoredRequestDTO());
        while (response.getNextCursor() != null) {
            response = this.tagService.retrieveAllTags(new CommonCursoredRequestDTO(response.getNextCursor(), ApplicationConst.TAGS_MAX_ITEMS_RETRIEVE));
        }
    }

    @Override
    public void reloadCategoriesInCache() {
        Optional.ofNullable(cacheManager.getCache(CacheConst.CACHE_NAME_CATEGORIES))
                .ifPresent(Cache::clear);
        var response = this.categoryService.retrieveAllCategories(new CommonCursoredRequestDTO());
        while (response.getNextCursor() != null) {
            response = this.categoryService.retrieveAllCategories(new CommonCursoredRequestDTO(response.getNextCursor(), ApplicationConst.CATEGORIES_MAX_ITEMS_RETRIEVE));
        }
    }

    @Override
    public void reloadLanguagesInCache() {
        Optional.ofNullable(cacheManager.getCache(CacheConst.CACHE_NAME_LANGUAGES))
                .ifPresent(Cache::clear);
        this.cursoredFileSystemService.retrieveAllLanguages();
    }

    @Override
    public void reloadPublishersInCache() {
        Optional.ofNullable(cacheManager.getCache(CacheConst.CACHE_NAME_PUBLISHERS))
                .ifPresent(Cache::clear);
        this.cursoredFileSystemService.retrieveAllPublishers();
    }
}
