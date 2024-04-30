package com.andreidodu.europealibrary.service;

public interface CacheLoader {
    void reload();

    void reloadExtensionsInCache();

    void reloadTagsInCache();

    void reloadCategoriesInCache();

    void reloadLanguagesInCache();

    void reloadPublishersInCache();
}
