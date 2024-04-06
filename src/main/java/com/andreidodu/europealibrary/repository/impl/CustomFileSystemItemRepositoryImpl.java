package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.CursorTypeRequestDTO;
import com.andreidodu.europealibrary.model.*;
import com.andreidodu.europealibrary.repository.CustomFileSystemItemRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
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
        int numberOfResults = cursorRequestDTO.getLimit() == null ? ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE : cursorRequestDTO.getLimit();

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.parent.id.eq(parentId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorId != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
        }

        if (numberOfResults > ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE) {
            numberOfResults = ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE;
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
    public List<FileSystemItem> retrieveChildrenByCursoredCategoryId(CursorRequestDTO cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParentId());

        Long categoryId = cursorRequestDTO.getParentId();
        Long cursorId = cursorRequestDTO.getNextCursor();
        int numberOfResults = cursorRequestDTO.getLimit() == null ? ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE : cursorRequestDTO.getLimit();

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;
        QCategory category = QCategory.category;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(category.id.eq(categoryId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorId != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
        }

        if (numberOfResults > ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE) {
            numberOfResults = ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE;
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
    public List<FileSystemItem> retrieveChildrenByCursoredTagId(CursorRequestDTO cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParentId());

        Long tagId = cursorRequestDTO.getParentId();
        Long cursorId = cursorRequestDTO.getNextCursor();
        int numberOfResults = cursorRequestDTO.getLimit() == null ? ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE : cursorRequestDTO.getLimit();

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;
        QTag tag = QTag.tag;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(tag.id.eq(tagId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorRequestDTO.getNextCursor() != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
        }

        if (cursorRequestDTO.getLimit() > ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE) {
            numberOfResults = ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE;
        }

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileSystemItem)
                .from(fileSystemItem, tag)
                .where(booleanBuilder.and(fileSystemItem.fileMetaInfo.tagList.contains(tag)))
                .limit(numberOfResults + 1)
                .orderBy(fileSystemItem.id.asc())
                .fetch();

    }


    @Override
    public List<FileSystemItem> retrieveChildrenByCursoredFileExtension(CursorTypeRequestDTO cursorTypeRequestDTO) {
        Objects.requireNonNull(cursorTypeRequestDTO.getExtension());

        final String extension = cursorTypeRequestDTO.getExtension();
        Long cursorId = cursorTypeRequestDTO.getNextCursor();
        int numberOfResults = cursorTypeRequestDTO.getLimit() == null ? ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE : cursorTypeRequestDTO.getLimit();

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.extension.eq(extension));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorTypeRequestDTO.getNextCursor() != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
        }

        if (cursorTypeRequestDTO.getLimit() > ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE) {
            numberOfResults = ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE;
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
    public List<FileExtensionProjection> retrieveExtensionsInfo() {
        QFileSystemItem fileSystemItem = new QFileSystemItem("outerFSI");
        QFileExtensionProjection fileExtensionProjection = createFileExtensionProjection(fileSystemItem);
        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileExtensionProjection)
                .from(fileSystemItem)
                .groupBy(fileSystemItem.extension)
                .orderBy(new OrderSpecifier<>(Order.DESC, Expressions.numberPath(Long.class, "cnt")))
                .having(fileSystemItem.extension.trim().length().gt(0))
                .fetch();
    }

    private QFileExtensionProjection createFileExtensionProjection(QFileSystemItem fileSystemItem) {
        return new QFileExtensionProjection(fileSystemItem.extension, Expressions.as(fileSystemItem.extension.count(), "cnt"),
                calculateNextCursorByFileExtensionSubQuery(fileSystemItem));
    }

    private Expression<Long> calculateNextCursorByFileExtensionSubQuery(QFileSystemItem parent) {
        QFileSystemItem innerFileSystemItem = new QFileSystemItem("innerFSI");
        return JPAExpressions
                .select(innerFileSystemItem.id.min())
                .from(innerFileSystemItem)
                .where(innerFileSystemItem.extension.eq(parent.extension));
    }
}
