package com.andreidodu.europealibrary.service;

import org.springframework.security.core.Authentication;

public interface CacheLoaderService {
    void reload(Authentication authentication);

    void reloadExtensionsInCache(Authentication authentication);

    void reloadTagsInCache(Authentication authentication);

    void reloadCategoriesInCache(Authentication authentication);

    void reloadLanguagesInCache(Authentication authentication);

    void reloadPublishersInCache(Authentication authentication);

    void reloadPublishedDatesInCache(Authentication authentication);
}
