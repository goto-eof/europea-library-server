package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.model.QCategory;
import com.andreidodu.europealibrary.model.QFileSystemItem;
import com.andreidodu.europealibrary.model.QTag;
import com.andreidodu.europealibrary.repository.CustomFileSystemItemRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Objects;

public class CustomFileSystemItemRepositoryImpl implements CustomFileSystemItemRepository {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<FileSystemItem> retrieveChildrenByCursor(CursorRequestDTO cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParentId());

        Long parentId = cursorRequestDTO.getParentId();
        Long cursorId = cursorRequestDTO.getNextCursor();
        int numberOfResults = cursorRequestDTO.getLimit() == null ? ApplicationConst.MAX_ITEMS_RETRIEVE : cursorRequestDTO.getLimit();

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.parent.id.eq(parentId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorId != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
        }

        if (numberOfResults > ApplicationConst.MAX_ITEMS_RETRIEVE) {
            numberOfResults = ApplicationConst.MAX_ITEMS_RETRIEVE;
        }

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileSystemItem)
                .from(fileSystemItem)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .orderBy(fileSystemItem.id.asc())
                .fetch();
    }

    @Override
    public List<FileSystemItem> retrieveChildrenByCategoryId(CursorRequestDTO cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParentId());

        Long parentId = cursorRequestDTO.getParentId();
        Long cursorId = cursorRequestDTO.getNextCursor();
        int numberOfResults = cursorRequestDTO.getLimit() == null ? ApplicationConst.MAX_ITEMS_RETRIEVE : cursorRequestDTO.getLimit();

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;
        QCategory category = QCategory.category;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(category.id.eq(parentId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorId != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
        }

        if (numberOfResults > ApplicationConst.MAX_ITEMS_RETRIEVE) {
            numberOfResults = ApplicationConst.MAX_ITEMS_RETRIEVE;
        }

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileSystemItem)
                .from(fileSystemItem, category)
                .where(booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.categoryList.contains(category)))
                .limit(numberOfResults + 1)
                .orderBy(fileSystemItem.id.asc())
                .fetch();
    }

    @Override
    public List<FileSystemItem> retrieveChildrenByTagId(CursorRequestDTO cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParentId());

        Long parentId = cursorRequestDTO.getParentId();
        Long cursorId = cursorRequestDTO.getNextCursor();
        int numberOfResults = cursorRequestDTO.getLimit() == null ? ApplicationConst.MAX_ITEMS_RETRIEVE : cursorRequestDTO.getLimit();

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;
        QTag tag = QTag.tag;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(tag.id.eq(parentId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorRequestDTO.getNextCursor() != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
        }

        if (cursorRequestDTO.getLimit() > ApplicationConst.MAX_ITEMS_RETRIEVE) {
            numberOfResults = ApplicationConst.MAX_ITEMS_RETRIEVE;
        }

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileSystemItem)
                .from(fileSystemItem, tag)
                .where(booleanBuilder.and(fileSystemItem.fileMetaInfo.tagList.contains(tag)).and(tag.id.eq(parentId)))
                .limit(numberOfResults + 1)
                .orderBy(fileSystemItem.id.asc())
                .fetch();

    }
}
