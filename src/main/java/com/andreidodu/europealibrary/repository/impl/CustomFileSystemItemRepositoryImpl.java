package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.enums.OrderEnum;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.model.*;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import com.andreidodu.europealibrary.repository.CustomFileSystemItemRepository;
import com.andreidodu.europealibrary.repository.common.CommonRepository;
import com.andreidodu.europealibrary.util.LimitUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomFileSystemItemRepositoryImpl extends CommonRepository implements CustomFileSystemItemRepository {
    @PersistenceContext
    private EntityManager entityManager;
    private final CategoryRepository categoryRepository;

    private final static QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;


    @Override
    public List<FileSystemItem> retrieveChildrenByCursor(CursorRequestDTO cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParentId());

        Long parentId = cursorRequestDTO.getParentId();
        Long cursorId = cursorRequestDTO.getNextCursor();
        int numberOfResults = LimitUtil.calculateLimit(cursorRequestDTO, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

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

        int numberOfResults = LimitUtil.calculateLimit(cursorRequestDTO, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        booleanBuilder.and(fileSystemItem.isDirectory.isNull().or(fileSystemItem.isDirectory.isFalse()));

        if (cursorId != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
        }

        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ValidationException("Category not found"));

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileSystemItem)
                .from(fileSystemItem)
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

        int numberOfResults = LimitUtil.calculateLimit(cursorRequestDTO, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;
        QTag tag = QTag.tag;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(tag.id.eq(tagId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        booleanBuilder.and(fileSystemItem.isDirectory.isNull().or(fileSystemItem.isDirectory.isFalse()));

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

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.extension.eq(cursorTypeRequestDTO.getExtension()));


        OrderSpecifier<?>[] customOrder = new OrderSpecifier[]{
                fileSystemItem.downloadCount.desc(), fileSystemItem.id.asc()
        };

        return this.basicRetrieve(cursorTypeRequestDTO.getNextCursor(), cursorTypeRequestDTO.getLimit(), booleanBuilder, customOrder, OrderEnum.ASC);
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
        return new QFileExtensionProjection(
                fileSystemItem.extension, Expressions.as(fileSystemItem.extension.count(), "cnt"),
                Expressions.as(fileSystemItem.id.min(), "minId")
        );
    }

    @Override
    public List<ItemAndFrequencyProjection> retrieveLanguagesInfo() {
        QFileSystemItem fileSystemItem = new QFileSystemItem("outerFSI");
        QItemAndFrequencyProjection projection = createItemAndFrequencyProjectionByLanguage(fileSystemItem);
        return new JPAQuery<FileSystemItem>(entityManager)
                .select(projection)
                .from(fileSystemItem)
                .groupBy(fileSystemItem.fileMetaInfo.bookInfo.language)
                .orderBy(new OrderSpecifier<>(Order.DESC, Expressions.numberPath(Long.class, "cnt")))
                .having(fileSystemItem.fileMetaInfo.bookInfo.language.trim().length().gt(0))
                .fetch();
    }

    @Override
    public List<FileSystemItem> retrieveChildrenByCursoredLanguage(GenericCursorRequestDTO<String> cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParent());

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.language.eq(cursorRequestDTO.getParent()));

        OrderSpecifier<?>[] customOrder = new OrderSpecifier[]{
                fileSystemItem.downloadCount.desc(), fileSystemItem.id.asc()
        };

        return this.basicRetrieve(cursorRequestDTO.getNextCursor(), cursorRequestDTO.getLimit(), booleanBuilder, customOrder, OrderEnum.ASC);
    }

    @Override
    public List<FileSystemItem> retrieveChildrenByCursoredPublisher(GenericCursorRequestDTO<String> cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParent());

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.publisher.eq(cursorRequestDTO.getParent()));

        OrderSpecifier<?>[] customOrder = new OrderSpecifier[]{
                fileSystemItem.downloadCount.desc(), fileSystemItem.id.asc()
        };

        return this.basicRetrieve(cursorRequestDTO.getNextCursor(), cursorRequestDTO.getLimit(), booleanBuilder, customOrder, OrderEnum.ASC);
    }

    @Override
    public List<ItemAndFrequencyProjection> retrievePublishedDatesInfo() {
        QFileSystemItem fileSystemItem = new QFileSystemItem("outerFSI");
        QItemAndFrequencyProjection projection = createItemAndFrequencyProjectionByPublishedDate(fileSystemItem);
        return new JPAQuery<FileSystemItem>(entityManager)
                .select(projection)
                .from(fileSystemItem)
                .groupBy(fileSystemItem.fileMetaInfo.bookInfo.publishedDate)
                .orderBy(new OrderSpecifier<>(Order.DESC, Expressions.numberPath(Long.class, "cnt")))
                .having(fileSystemItem.fileMetaInfo.bookInfo.publishedDate.trim().length().gt(0))
                .fetch();
    }

    private QItemAndFrequencyProjection createItemAndFrequencyProjectionByPublishedDate(QFileSystemItem fileSystemItem) {
        return new QItemAndFrequencyProjection(fileSystemItem.fileMetaInfo.bookInfo.publishedDate, Expressions.as(fileSystemItem.fileMetaInfo.bookInfo.publishedDate.count(), "cnt"),
                Expressions.as(fileSystemItem.id.min(), "minId"));
    }

    private QItemAndFrequencyProjection createItemAndFrequencyProjectionByLanguage(QFileSystemItem fileSystemItem) {
        return new QItemAndFrequencyProjection(fileSystemItem.fileMetaInfo.bookInfo.language, Expressions.as(fileSystemItem.fileMetaInfo.bookInfo.language.count(), "cnt"),
                Expressions.as(fileSystemItem.id.min(), "minId"));
    }

    private Expression<Long> calculateNextCursorByLanguageSubQuery(QFileSystemItem parent) {
        QFileSystemItem innerFileSystemItem = new QFileSystemItem("innerFSI");
        return JPAExpressions
                .select(innerFileSystemItem.id.min())
                .from(innerFileSystemItem)
                .where(innerFileSystemItem.fileMetaInfo.bookInfo.language.eq(parent.fileMetaInfo.bookInfo.language));
    }


    @Override
    public List<ItemAndFrequencyProjection> retrievePublishersInfo() {
        QFileSystemItem fileSystemItem = new QFileSystemItem("outerFSI");
        QItemAndFrequencyProjection projection = createItemAndFrequencyByPublisherProjection(fileSystemItem);
        return new JPAQuery<FileSystemItem>(entityManager)
                .select(projection)
                .from(fileSystemItem)
                .groupBy(fileSystemItem.fileMetaInfo.bookInfo.publisher)
                .orderBy(new OrderSpecifier<>(Order.DESC, Expressions.numberPath(Long.class, "cnt")))
                .having(fileSystemItem.fileMetaInfo.bookInfo.publisher.trim().length().gt(0))
                .fetch();
    }

    private QItemAndFrequencyProjection createItemAndFrequencyByPublisherProjection(QFileSystemItem fileSystemItem) {
        return new QItemAndFrequencyProjection(
                fileSystemItem.fileMetaInfo.bookInfo.publisher,
                Expressions.as(fileSystemItem.fileMetaInfo.bookInfo.publisher.count(), "cnt"),
                Expressions.as(fileSystemItem.id.min(), "minId")
        );
    }

    private Expression<Long> calculateNextCursorByPublisherSubQuery(QFileSystemItem parent) {
        QFileSystemItem innerFileSystemItem = new QFileSystemItem("innerFSI");
        return JPAExpressions
                .select(innerFileSystemItem.id.min())
                .from(innerFileSystemItem)
                .where(innerFileSystemItem.fileMetaInfo.bookInfo.publisher.eq(parent.fileMetaInfo.bookInfo.publisher));
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

        int numberOfResults = LimitUtil.calculateLimit(searchFileSystemItemRequestDTO, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileSystemItem)
                .from(fileSystemItem)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .orderBy(fileSystemItem.id.asc())
                .fetch();
    }

    private static BooleanBuilder applySearchFilters(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, QFileSystemItem fileSystemItem) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        filterByNextCursorIfNecessary(searchFileSystemItemRequestDTO, booleanBuilder, fileSystemItem);

        filterByTitleIfNecessary(searchFileSystemItemRequestDTO, booleanBuilder, fileSystemItem);

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
                .map(String::trim)
                .filter(item -> !item.trim().isEmpty())
                .ifPresent(description -> booleanBuilder.and(fileSystemItem.fileMetaInfo.description.containsIgnoreCase(description)));
    }

    private static void filterByAuthorIfNecessary(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, BooleanBuilder booleanBuilder, QFileSystemItem fileSystemItem) {
        Optional.ofNullable(searchFileSystemItemRequestDTO.getAuthor())
                .map(String::trim)
                .filter(item -> !item.trim().isEmpty())
                .ifPresent(author -> Arrays.stream(author.split(" "))
                        .forEach(authorItem -> booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.authors.containsIgnoreCase(author))));
    }

    private static void filterByPublisherIfNecessary(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, BooleanBuilder booleanBuilder, QFileSystemItem fileSystemItem) {
        Optional.ofNullable(searchFileSystemItemRequestDTO.getPublisher())
                .map(String::trim)
                .filter(item -> !item.trim().isEmpty())
                .ifPresent(publisher -> booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.publisher.equalsIgnoreCase(publisher)));
    }

    private static void filterByIsbnIfNecessary(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, BooleanBuilder booleanBuilder, QFileSystemItem fileSystemItem) {
        Optional.ofNullable(searchFileSystemItemRequestDTO.getIsbn())
                .map(String::trim)
                .filter(item -> !item.trim().isEmpty())
                .ifPresent(isbn ->
                        booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.isbn10.equalsIgnoreCase(isbn).or(fileSystemItem.fileMetaInfo.bookInfo.isbn13.equalsIgnoreCase(isbn)))
                );
    }

    private static void filterByTitleIfNecessary(SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO, BooleanBuilder booleanBuilder, QFileSystemItem fileSystemItem) {
        Optional.ofNullable(searchFileSystemItemRequestDTO.getTitle())
                .map(String::trim)
                .filter(item -> !item.trim().isEmpty())
                .ifPresent(title -> booleanBuilder.and(fileSystemItem.fileMetaInfo.title.containsIgnoreCase(title)));
    }

    @Override
    public List<FileSystemItem> retrieveChildrenByCursoredPublishedDate(GenericCursorRequestDTO<String> cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParent());

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.publishedDate.eq(cursorRequestDTO.getParent()));

        OrderSpecifier<?>[] customOrder = new OrderSpecifier[]{
                fileSystemItem.id.asc()
        };

        return this.basicRetrieve(cursorRequestDTO.getNextCursor(), cursorRequestDTO.getLimit(), booleanBuilder, customOrder, OrderEnum.ASC);
    }

    @Override
    public List<FileSystemItem> retrieveChildrenByCursoredRating(CursorRequestDTO cursorRequestDTO) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.averageRating.isNotNull());

        OrderSpecifier<?>[] customOrder = new OrderSpecifier[]{
                fileSystemItem.fileMetaInfo.bookInfo.averageRating.desc(), fileSystemItem.fileMetaInfo.bookInfo.ratingsCount.desc()
        };

        return this.basicRetrieve(cursorRequestDTO.getNextCursor(), cursorRequestDTO.getLimit(), booleanBuilder, customOrder, OrderEnum.ASC);
    }

    @Override
    public List<FileSystemItem> retrieveCursoredByDownloadCount(CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO) {

        BooleanBuilder customWhere = new BooleanBuilder();

        Optional.ofNullable(cursoredRequestByFileTypeDTO.getFileType())
                .ifPresent((fileType) -> customWhere.and(fileSystemItem.extension.equalsIgnoreCase(cursoredRequestByFileTypeDTO.getFileType())));

        OrderSpecifier<?>[] customOrder = new OrderSpecifier[]{
                fileSystemItem.downloadCount.desc(), fileSystemItem.id.asc()
        };

        return this.basicRetrieve(cursoredRequestByFileTypeDTO.getNextCursor(), cursoredRequestByFileTypeDTO.getLimit(), customWhere, customOrder, OrderEnum.ASC);
    }

    @Override
    public List<FileSystemItem> retrieveNewCursored(CursorCommonRequestDTO commonRequestDTO) {
        OrderSpecifier<?>[] order = new OrderSpecifier[]{
                fileSystemItem.createdDate.desc(),
                fileSystemItem.id.desc()
        };

        return this.basicRetrieve(commonRequestDTO.getNextCursor(), commonRequestDTO.getLimit(), new BooleanBuilder(), order, OrderEnum.DESC);
    }

    public List<FileSystemItem> basicRetrieve(Long cursorId, Integer limit, BooleanBuilder customWhere, OrderSpecifier<?>[] customOrder, OrderEnum order) {
        int numberOfResults = LimitUtil.calculateLimit(limit, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        booleanBuilder.and(fileSystemItem.isDirectory.isNull().or(fileSystemItem.isDirectory.isFalse()));
        Optional.ofNullable(cursorId).ifPresent((cursorIdValue) -> {
                    if (OrderEnum.ASC.equals(order)) {
                        booleanBuilder.and(fileSystemItem.id.goe(cursorIdValue));
                    } else {
                        booleanBuilder.and(fileSystemItem.id.loe(cursorIdValue));
                    }
                }
        );

        booleanBuilder.and(customWhere);

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileSystemItem)
                .from(fileSystemItem)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .orderBy(customOrder)
                .fetch();
    }

}
