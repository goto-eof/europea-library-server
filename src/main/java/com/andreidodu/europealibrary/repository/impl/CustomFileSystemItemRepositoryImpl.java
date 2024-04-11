package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CursorRequestDTO;
import com.andreidodu.europealibrary.dto.CursorTypeRequestDTO;
import com.andreidodu.europealibrary.dto.SearchFileSystemItemRequestDTO;
import com.andreidodu.europealibrary.dto.common.Limitable;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CustomFileSystemItemRepositoryImpl implements CustomFileSystemItemRepository {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<FileSystemItem> retrieveChildrenByCursor(CursorRequestDTO cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParentId());

        Long parentId = cursorRequestDTO.getParentId();
        Long cursorId = cursorRequestDTO.getNextCursor();
        int numberOfResults = calculateExplorerLimit(cursorRequestDTO);

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.parent.id.eq(parentId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorId != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
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
        int numberOfResults = calculateExplorerLimit(cursorRequestDTO);

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;
        QCategory category = QCategory.category;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(category.id.eq(categoryId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorId != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
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

        int numberOfResults = calculateExplorerLimit(cursorRequestDTO);

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;
        QTag tag = QTag.tag;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(tag.id.eq(tagId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorRequestDTO.getNextCursor() != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
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
        int numberOfResults = calculateExplorerLimit(cursorTypeRequestDTO);

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.extension.eq(extension));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorTypeRequestDTO.getNextCursor() != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
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

    @Override
    public List<FileSystemItem> search(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO) {

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;

        BooleanBuilder booleanBuilder = applySearchFilters(searchFileSystemItemRequestDTO, fileSystemItem);

        final int numberOfResults = calculateExplorerLimit(searchFileSystemItemRequestDTO);

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileSystemItem)
                .from(fileSystemItem)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .orderBy(fileSystemItem.id.asc())
                .fetch();
    }

    private <T extends Limitable> int calculateExplorerLimit(T dto) {
        return dto.getLimit() == null ? ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE : dto.getLimit();
    }

    private static BooleanBuilder applySearchFilters(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, QFileSystemItem fileSystemItem) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        filterByNextCursorIfNecessary(searchFileSystemItemRequestDTO, booleanBuilder, fileSystemItem);

        fileterByTitleIfNecessary(searchFileSystemItemRequestDTO, booleanBuilder, fileSystemItem);

        filterByDescriptionIfNecessary(searchFileSystemItemRequestDTO, booleanBuilder, fileSystemItem);

        filterByYearIfNecessary(searchFileSystemItemRequestDTO, booleanBuilder, fileSystemItem);

        filterByIsbnIfNecessary(searchFileSystemItemRequestDTO, booleanBuilder, fileSystemItem);

        filterByPublisherIfNecessary(searchFileSystemItemRequestDTO, booleanBuilder, fileSystemItem);

        filterByAuthorIfNecessary(searchFileSystemItemRequestDTO, booleanBuilder, fileSystemItem);

        return booleanBuilder;
    }

    private static void filterByNextCursorIfNecessary(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, BooleanBuilder booleanBuilder, QFileSystemItem fileSystemItem) {
        final Long cursorId = searchFileSystemItemRequestDTO.getNextCursor();
        Optional.ofNullable(cursorId)
                .ifPresent(nextCursor -> booleanBuilder.and(fileSystemItem.id.goe(nextCursor)));
    }

    private static void filterByYearIfNecessary(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, BooleanBuilder booleanBuilder, QFileSystemItem fileSystemItem) {
        Optional.ofNullable(searchFileSystemItemRequestDTO.getYear())
                .ifPresent(year -> booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.publishedDate.containsIgnoreCase(year.toString())));
    }

    private static void filterByDescriptionIfNecessary(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, BooleanBuilder booleanBuilder, QFileSystemItem fileSystemItem) {
        Optional.ofNullable(searchFileSystemItemRequestDTO.getDescription())
                .ifPresent(description -> booleanBuilder.and(fileSystemItem.fileMetaInfo.description.containsIgnoreCase(description)));
    }

    private static void filterByAuthorIfNecessary(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, BooleanBuilder booleanBuilder, QFileSystemItem fileSystemItem) {
        Optional.ofNullable(searchFileSystemItemRequestDTO.getAuthor())
                .ifPresent(author -> Arrays.stream(author.split(" "))
                        .forEach(authorItem -> booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.authors.containsIgnoreCase(author))));
    }

    private static void filterByPublisherIfNecessary(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, BooleanBuilder booleanBuilder, QFileSystemItem fileSystemItem) {
        Optional.ofNullable(searchFileSystemItemRequestDTO.getPublisher())
                .ifPresent(publisher -> booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.publisher.equalsIgnoreCase(publisher)));
    }

    private static void filterByIsbnIfNecessary(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, BooleanBuilder booleanBuilder, QFileSystemItem fileSystemItem) {
        Optional.ofNullable(searchFileSystemItemRequestDTO.getIsbn())
                .ifPresent(isbn ->
                        booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.isbn10.equalsIgnoreCase(isbn).or(fileSystemItem.fileMetaInfo.bookInfo.isbn13.equalsIgnoreCase(isbn)))
                );
    }

    private static void fileterByTitleIfNecessary(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, BooleanBuilder booleanBuilder, QFileSystemItem fileSystemItem) {
        Optional.ofNullable(searchFileSystemItemRequestDTO.getTitle())
                .ifPresent(title -> booleanBuilder.and(fileSystemItem.fileMetaInfo.title.containsIgnoreCase(title)));
    }
}
