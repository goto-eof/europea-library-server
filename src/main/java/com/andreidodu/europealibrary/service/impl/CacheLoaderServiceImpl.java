package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.constants.CacheConst;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.service.CacheLoaderService;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheLoaderServiceImpl implements CacheLoaderService {
    private final CursoredFileSystemService cursoredFileSystemService;
    private final CategoryServiceImpl categoryService;
    private final TagServiceImpl tagService;
    private final CacheManager cacheManager;

    @Override
    public void reload(Authentication authentication) {
        log.info("loading cache");

        reloadExtensionsInCache(authentication);
        reloadCategoriesInCache(authentication);
        reloadTagsInCache(authentication);
        reloadLanguagesInCache(authentication);
        reloadPublishersInCache(authentication);
        reloadPublishedDatesInCache(authentication);

        log.info("cache loaded");
    }

    @Override
    public void reloadExtensionsInCache(Authentication authentication) {
        Optional.ofNullable(cacheManager.getCache(CacheConst.CACHE_NAME_EXTENSIONS))
                .ifPresent(Cache::clear);
        this.cursoredFileSystemService.getAllExtensions(authentication);
    }

    @Override
    public void reloadTagsInCache(Authentication authentication) {
        Optional.ofNullable(cacheManager.getCache(CacheConst.CACHE_NAME_TAGS))
                .ifPresent(Cache::clear);
        var response = this.tagService.retrieveAllTags(authentication, new CommonCursoredRequestDTO());
        while (response.getNextCursor() != null) {
            response = this.tagService.retrieveAllTags(authentication, new CommonCursoredRequestDTO(response.getNextCursor(), ApplicationConst.TAGS_MAX_ITEMS_RETRIEVE));
        }
    }

    @Override
    public void reloadCategoriesInCache(Authentication authentication) {
        Optional.ofNullable(cacheManager.getCache(CacheConst.CACHE_NAME_CATEGORIES))
                .ifPresent(Cache::clear);
        var response = this.categoryService.retrieveAllCategories(authentication, new CommonCursoredRequestDTO());
        while (response.getNextCursor() != null) {
            response = this.categoryService.retrieveAllCategories(authentication, new CommonCursoredRequestDTO(response.getNextCursor(), ApplicationConst.CATEGORIES_MAX_ITEMS_RETRIEVE));
        }
    }

    @Override
    public void reloadLanguagesInCache(Authentication authentication) {
        Optional.ofNullable(cacheManager.getCache(CacheConst.CACHE_NAME_LANGUAGES))
                .ifPresent(Cache::clear);
        this.cursoredFileSystemService.retrieveAllLanguages(authentication);
    }

    @Override
    public void reloadPublishersInCache(Authentication authentication) {
        Optional.ofNullable(cacheManager.getCache(CacheConst.CACHE_NAME_PUBLISHERS))
                .ifPresent(Cache::clear);
        this.cursoredFileSystemService.retrieveAllPublishers(authentication);
    }

    @Override
    public void reloadPublishedDatesInCache(Authentication authentication) {
        Optional.ofNullable(cacheManager.getCache(CacheConst.CACHE_NAME_PUBLISHED_DATES))
                .ifPresent(Cache::clear);
        this.cursoredFileSystemService.retrieveAllPublishedDates(authentication);
    }
}
