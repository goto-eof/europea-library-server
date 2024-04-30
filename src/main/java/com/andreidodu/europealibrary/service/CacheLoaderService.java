package com.andreidodu.europealibrary.service;

public interface CacheLoaderService {
    void reload();

    void reloadExtensionsInCache();

    void reloadTagsInCache();

    void reloadCategoriesInCache();

    void reloadLanguagesInCache();

    void reloadPublishersInCache();

    void reloadPublishedDatesInCache();
}
