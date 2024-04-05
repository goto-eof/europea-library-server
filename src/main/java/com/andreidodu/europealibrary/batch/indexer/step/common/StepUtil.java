package com.andreidodu.europealibrary.batch.indexer.step.common;

import com.andreidodu.europealibrary.batch.indexer.step.externalapi.dataretriever.strategy.CategoryUtil;
import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy.TagUtil;
import com.andreidodu.europealibrary.constants.DataPropertiesConst;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.BookInfoRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class StepUtil {
    private final TagUtil tagUtil;
    private final CategoryUtil categoryUtil;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final BookInfoRepository bookInfoRepository;

    public Set<Tag> createOrLoadItems(List<String> items) {
        return Optional.ofNullable(items)
                .map(itemsList -> itemsList.stream()
                        .map(this::createOrLoadTagEntity)
                        .collect(Collectors.toSet()))
                .orElse(new HashSet<>());
    }

    private Tag createOrLoadTagEntity(String tagName) {
        return tagUtil.createTagEntity(tagName);
    }


    public Set<String> explodeInUniqueItems(List<String> itemNameList) {
        return itemNameList.stream()
                .filter(tagName -> !StringUtil.clean(tagName.trim()).isEmpty())
                .map(StringUtil::clean)
                .map(StringUtil::splitString)
                .flatMap(Collection::stream)
                .map(StringUtil::cleanAndTrimToNull)
                .filter(Objects::nonNull)
                .map(tagName -> tagName.substring(0, Math.min(tagName.length(), DataPropertiesConst.TAG_NAME_MAX_LENGTH)))
                .map(StringUtil::cleanAndTrimToNull)
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    public FileMetaInfo associateTags(FileMetaInfo savedFileMetaInfo, Set<Tag> explodedTags) {
        try {
            List<Tag> tagEntityList = savedFileMetaInfo.getTagList();
            Set<Long> entityIdList = tagEntityList.stream()
                    .map(Tag::getId)
                    .collect(Collectors.toSet());
            Set<Tag> tagsToAdd = explodedTags.stream()
                    .filter(tag -> !entityIdList.contains(tag.getId()))
                    .collect(Collectors.toSet());
            tagEntityList.addAll(tagsToAdd);
            return this.fileMetaInfoRepository.save(savedFileMetaInfo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("ciao");
        }
    }

    public Set<Category> createOrLoadCategories(List<String> categories) {
        return Optional.ofNullable(categories)
                .map(itemsList -> itemsList.stream()
                        .map(this::createOrLoadCategoryEntity)
                        .collect(Collectors.toSet()))
                .orElse(new HashSet<>());
    }

    private Category createOrLoadCategoryEntity(String categoryName) {
        return categoryUtil.createCategoryEntity(categoryName);
    }

    public BookInfo associateCategories(BookInfo bookInfo, Set<Category> explodedCategories) {
        try {
            List<Category> categoryEntityList = bookInfo.getCategoryList();
            Set<Long> entityIdList = categoryEntityList.stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
            Set<Category> categoriesToAdd = explodedCategories.stream()
                    .filter(category -> !entityIdList.contains(category.getId()))
                    .collect(Collectors.toSet());
            categoryEntityList.addAll(categoriesToAdd);
            return this.bookInfoRepository.save(bookInfo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("ciao");
        }
    }
}
