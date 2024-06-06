package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.enums.OrderEnum;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.model.*;
import com.andreidodu.europealibrary.model.security.User;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import com.andreidodu.europealibrary.repository.CustomFileSystemItemRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemTopSoldViewRepository;
import com.andreidodu.europealibrary.repository.common.CommonRepository;
import com.andreidodu.europealibrary.repository.util.CursoredUtil;
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
    private final FileSystemItemTopSoldViewRepository fileSystemItemTopSoldViewRepository;

    private final static QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;


    @Override
    public List<FileSystemItem> retrieveChildrenByCursor(PaginatedExplorerOptions paginatedExplorerOptions, CursorRequestDTO cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParentId());

        Long parentId = cursorRequestDTO.getParentId();
        Long cursorId = cursorRequestDTO.getNextCursor();
        int numberOfResults = LimitUtil.calculateLimit(cursorRequestDTO, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        applyCommonFilter(fileSystemItem, paginatedExplorerOptions, booleanBuilder);
        booleanBuilder.and(fileSystemItem.parent.id.eq(parentId));
        booleanBuilder.and(fileSystemItem.jobStep.eq(JobStepEnum.READY.getStepNumber()));
        if (cursorId != null) {
            booleanBuilder.and(fileSystemItem.id.goe(cursorId));
        }

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileSystemItem)
                .from(fileSystemItem)
                .leftJoin(fileSystemItem.fileMetaInfo, QFileMetaInfo.fileMetaInfo)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .orderBy(fileSystemItem.id.asc())
                .fetch();
    }

    @Override
    public List<FileSystemItem> retrieveChildrenByCursoredCategoryId(PaginatedExplorerOptions paginatedExplorerOptions, CursorRequestDTO cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParentId());

        Long categoryId = cursorRequestDTO.getParentId();
        Long cursorId = cursorRequestDTO.getNextCursor();

        int numberOfResults = LimitUtil.calculateLimit(cursorRequestDTO, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        applyCommonFilter(fileSystemItem, paginatedExplorerOptions, booleanBuilder);

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
    public List<FileSystemItem> retrieveChildrenByCursoredTagId(PaginatedExplorerOptions paginatedExplorerOptions, CursorRequestDTO cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParentId());

        Long tagId = cursorRequestDTO.getParentId();
        Long cursorId = cursorRequestDTO.getNextCursor();

        int numberOfResults = LimitUtil.calculateLimit(cursorRequestDTO, ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;
        QTag tag = QTag.tag;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        applyCommonFilter(fileSystemItem, paginatedExplorerOptions, booleanBuilder);

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
    public List<FileSystemItem> retrieveChildrenByCursoredFileExtension(PaginatedExplorerOptions paginatedExplorerOptions, CursorTypeRequestDTO cursorTypeRequestDTO) {
        Objects.requireNonNull(cursorTypeRequestDTO.getExtension());

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.extension.eq(cursorTypeRequestDTO.getExtension()));
        applyCommonFilter(fileSystemItem, paginatedExplorerOptions, booleanBuilder);

        OrderSpecifier<?>[] customOrder = new OrderSpecifier[]{
                fileSystemItem.downloadCount.desc(), fileSystemItem.id.asc()
        };

        return this.basicRetrieve(cursorTypeRequestDTO.getNextCursor(), cursorTypeRequestDTO.getLimit(), booleanBuilder, customOrder, OrderEnum.ASC);
    }

    @Override
    public List<FileExtensionProjection> retrieveExtensionsInfo(PaginatedExplorerOptions paginatedExplorerOptions) {
        QFileSystemItem fileSystemItem = new QFileSystemItem("outerFSI");
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QFileMetaInfo fileMetaInfo = QFileMetaInfo.fileMetaInfo;
        applyCommonFilter(fileMetaInfo, paginatedExplorerOptions, booleanBuilder);
        QFileExtensionProjection fileExtensionProjection = createFileExtensionProjection(fileSystemItem);
        return new JPAQuery<FileSystemItem>(entityManager)
                .select(fileExtensionProjection)
                .from(fileSystemItem)
                .innerJoin(fileSystemItem.fileMetaInfo, fileMetaInfo)
                .where(booleanBuilder)
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
    public List<ItemAndFrequencyProjection> retrieveLanguagesInfo(PaginatedExplorerOptions paginatedExplorerOptions) {
        QFileSystemItem fileSystemItem = new QFileSystemItem("outerFSI");
        QItemAndFrequencyProjection projection = createItemAndFrequencyProjectionByLanguage(fileSystemItem);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QFileMetaInfo fileMetaInfo = QFileMetaInfo.fileMetaInfo;
        applyCommonFilter(fileMetaInfo, paginatedExplorerOptions, booleanBuilder);
        return new JPAQuery<FileSystemItem>(entityManager)
                .select(projection)
                .from(fileSystemItem)
                .innerJoin(fileSystemItem.fileMetaInfo, fileMetaInfo)
                .where(booleanBuilder)
                .groupBy(fileSystemItem.fileMetaInfo.bookInfo.language)
                .orderBy(new OrderSpecifier<>(Order.DESC, Expressions.numberPath(Long.class, "cnt")))
                .having(fileSystemItem.fileMetaInfo.bookInfo.language.trim().length().gt(0))
                .fetch();
    }

    @Override
    public List<FileSystemItem> retrieveChildrenByCursoredLanguage(PaginatedExplorerOptions paginatedExplorerOptions, GenericCursorRequestDTO<String> cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParent());

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.language.eq(cursorRequestDTO.getParent()));
        applyCommonFilter(fileSystemItem, paginatedExplorerOptions, booleanBuilder);

        OrderSpecifier<?>[] customOrder = new OrderSpecifier[]{
                fileSystemItem.downloadCount.desc(), fileSystemItem.id.asc()
        };

        return this.basicRetrieve(cursorRequestDTO.getNextCursor(), cursorRequestDTO.getLimit(), booleanBuilder, customOrder, OrderEnum.ASC);
    }

    @Override
    public List<FileSystemItem> retrieveChildrenByCursoredPublisher(PaginatedExplorerOptions paginatedExplorerOptions, GenericCursorRequestDTO<String> cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParent());

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.publisher.eq(cursorRequestDTO.getParent()));
        applyCommonFilter(fileSystemItem, paginatedExplorerOptions, booleanBuilder);

        OrderSpecifier<?>[] customOrder = new OrderSpecifier[]{
                fileSystemItem.downloadCount.desc(), fileSystemItem.id.asc()
        };

        return this.basicRetrieve(cursorRequestDTO.getNextCursor(), cursorRequestDTO.getLimit(), booleanBuilder, customOrder, OrderEnum.ASC);
    }

    @Override
    public List<ItemAndFrequencyProjection> retrievePublishedDatesInfo(PaginatedExplorerOptions paginatedExplorerOptions) {
        QFileSystemItem fileSystemItem = new QFileSystemItem("outerFSI");
        QItemAndFrequencyProjection projection = createItemAndFrequencyProjectionByPublishedDate(fileSystemItem);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QFileMetaInfo fileMetaInfo = QFileMetaInfo.fileMetaInfo;
        applyCommonFilter(fileMetaInfo, paginatedExplorerOptions, booleanBuilder);
        return new JPAQuery<FileSystemItem>(entityManager)
                .select(projection)
                .from(fileSystemItem)
                .innerJoin(fileSystemItem.fileMetaInfo, fileMetaInfo)
                .where(booleanBuilder)
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
    public List<ItemAndFrequencyProjection> retrievePublishersInfo(PaginatedExplorerOptions paginatedExplorerOptions) {
        QFileSystemItem fileSystemItem = new QFileSystemItem("outerFSI");
        QItemAndFrequencyProjection projection = createItemAndFrequencyByPublisherProjection(fileSystemItem);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QFileMetaInfo fileMetaInfo = QFileMetaInfo.fileMetaInfo;
        applyCommonFilter(fileMetaInfo, paginatedExplorerOptions, booleanBuilder);
        return new JPAQuery<FileSystemItem>(entityManager)
                .select(projection)
                .from(fileSystemItem)
                .innerJoin(fileSystemItem.fileMetaInfo, fileMetaInfo)
                .where(booleanBuilder)
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
    public List<FileSystemItem> search(PaginatedExplorerOptions paginatedExplorerOptions, SearchFileSystemItemRequestDTO searchFileSystemItemRequestDTO) {

        QFileSystemItem fileSystemItem = QFileSystemItem.fileSystemItem;

        BooleanBuilder booleanBuilder = applySearchFilters(searchFileSystemItemRequestDTO, fileSystemItem);
        applyCommonFilter(fileSystemItem, paginatedExplorerOptions, booleanBuilder);

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
    public List<FileSystemItem> retrieveChildrenByCursoredPublishedDate(PaginatedExplorerOptions paginatedExplorerOptions, GenericCursorRequestDTO<String> cursorRequestDTO) {
        Objects.requireNonNull(cursorRequestDTO.getParent());

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(fileSystemItem.fileMetaInfo.bookInfo.publishedDate.eq(cursorRequestDTO.getParent()));
        applyCommonFilter(fileSystemItem, paginatedExplorerOptions, booleanBuilder);

        OrderSpecifier<?>[] customOrder = new OrderSpecifier[]{
                fileSystemItem.id.asc()
        };

        return this.basicRetrieve(cursorRequestDTO.getNextCursor(), cursorRequestDTO.getLimit(), booleanBuilder, customOrder, OrderEnum.ASC);
    }

    @Override
    public PairDTO<List<PairDTO<FileSystemItem, PairDTO<Double, Long>>>, Long> retrieveChildrenByCursoredRating(PaginatedExplorerOptions paginatedExplorerOptions, CursorCommonRequestDTO cursorRequestDTO) {
        int numberOfResults = LimitUtil.calculateLimit(cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QFileSystemItemTopRatedView qFileSystemItemTopRatedView = QFileSystemItemTopRatedView.fileSystemItemTopRatedView;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        applyCommonFilter(qFileSystemItemTopRatedView.fileSystemItem, paginatedExplorerOptions, booleanBuilder);

        Long startRowNumber = CursoredUtil.calculateRowNumber(cursorRequestDTO.getNextCursor());
        Long endRowNumber = CursoredUtil.calculateMaxRowNumber(startRowNumber, numberOfResults);

        booleanBuilder.and(qFileSystemItemTopRatedView.id.goe(startRowNumber).and(qFileSystemItemTopRatedView.id.lt(endRowNumber)));

        List<FileSystemItemTopRatedView> fileSystemItemTopRatedViewList = new JPAQuery<List<FileSystemItemTopDownloadsView>>(entityManager)
                .select(qFileSystemItemTopRatedView)
                .from(qFileSystemItemTopRatedView)
                .where(booleanBuilder)
                .fetch();

        if (fileSystemItemTopRatedViewList.isEmpty() ||
                fileSystemItemTopRatedViewList.size() < numberOfResults ||
                startRowNumber > fileSystemItemTopRatedViewList.get(fileSystemItemTopRatedViewList.size() - 1).getId()) {
            endRowNumber = null;
        }

        List<PairDTO<FileSystemItem, PairDTO<Double, Long>>> children = fileSystemItemTopRatedViewList
                .stream()
                .map(item -> new PairDTO<FileSystemItem, PairDTO<Double, Long>>(item.getFileSystemItem(), new PairDTO<Double, Long>(item.getAverageRating(), item.getRatingsCount())))
                .toList();

        return new PairDTO<>(children, endRowNumber);
    }

    @Override
    public PairDTO<List<FileSystemItem>, Long> retrieveCursoredByDownloadCount(PaginatedExplorerOptions paginatedExplorerOptions, CursoredRequestByFileTypeDTO cursoredRequestByFileTypeDTO) {

        int numberOfResults = LimitUtil.calculateLimit(cursoredRequestByFileTypeDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QFileSystemItemTopDownloadsView fileSystemItemTopDownloadsView = QFileSystemItemTopDownloadsView.fileSystemItemTopDownloadsView;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        applyCommonFilter(fileSystemItemTopDownloadsView.fileSystemItem, paginatedExplorerOptions, booleanBuilder);

        Optional.ofNullable(cursoredRequestByFileTypeDTO.getFileType())
                .ifPresent(extension -> booleanBuilder.and(fileSystemItemTopDownloadsView.fileSystemItem.extension.eq(extension)));

        Long startRowNumber = CursoredUtil.calculateRowNumber(cursoredRequestByFileTypeDTO.getNextCursor());
        Long endRowNumber = CursoredUtil.calculateMaxRowNumber(startRowNumber, numberOfResults);

        booleanBuilder.and(fileSystemItemTopDownloadsView.id.goe(startRowNumber).and(fileSystemItemTopDownloadsView.id.lt(endRowNumber)));

        List<FileSystemItemTopDownloadsView> fileSystemItemTopDownloadsViewList = new JPAQuery<List<FileSystemItemTopDownloadsView>>(entityManager)
                .select(fileSystemItemTopDownloadsView)
                .from(fileSystemItemTopDownloadsView)
                .where(booleanBuilder)
                .orderBy(fileSystemItemTopDownloadsView.id.asc())
                .fetch();

        if (fileSystemItemTopDownloadsViewList.isEmpty() || fileSystemItemTopDownloadsViewList.size() < numberOfResults || startRowNumber > fileSystemItemTopDownloadsViewList.get(fileSystemItemTopDownloadsViewList.size() - 1).getId()) {
            endRowNumber = null;
        }

        List<FileSystemItem> children = fileSystemItemTopDownloadsViewList
                .stream().map(FileSystemItemTopDownloadsView::getFileSystemItem)
                .toList();

        return new PairDTO<>(children, endRowNumber);
    }


    public List<FileSystemItem> retrieveNewCursored(PaginatedExplorerOptions paginatedExplorerOptions, CursorCommonRequestDTO commonRequestDTO) {
        OrderSpecifier<?>[] order = new OrderSpecifier[]{
                fileSystemItem.createdDate.desc(),
                fileSystemItem.id.desc()
        };
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        applyCommonFilter(fileSystemItem, paginatedExplorerOptions, booleanBuilder);

        return this.basicRetrieve(commonRequestDTO.getNextCursor(), commonRequestDTO.getLimit(), booleanBuilder, order, OrderEnum.DESC);
    }

    private void applyCommonFilter(QFileSystemItem fileSystemItem, PaginatedExplorerOptions paginatedExplorerOptions, BooleanBuilder booleanBuilder) {
        super.applyCommonFilter(fileSystemItem.fileMetaInfo, paginatedExplorerOptions, booleanBuilder);
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


    @Override
    public PairDTO<List<PairDTO<FileSystemItem, Long>>, Long> retrieveCursoredByTopSold(PaginatedExplorerOptions paginatedExplorerOptions, CommonCursoredRequestDTO commonCursoredRequestDTO) {
        int numberOfResults = LimitUtil.calculateLimit(commonCursoredRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        QFileSystemItemTopSoldView topSold = QFileSystemItemTopSoldView.fileSystemItemTopSoldView;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        applyCommonFilter(topSold.fileSystemItem, paginatedExplorerOptions, booleanBuilder);

        Long nextCursor = commonCursoredRequestDTO.getNextCursor() != null ? commonCursoredRequestDTO.getNextCursor() : new JPAQuery<Long>(entityManager)
                .select(topSold.id.min())
                .from(topSold)
                .fetch().getFirst();
        Long startId = CursoredUtil.calculateRowNumber(nextCursor);
        booleanBuilder.and(topSold.id.goe(startId));

        List<FileSystemItemTopSoldView> userList = new JPAQuery<List<User>>(entityManager)
                .select(topSold)
                .from(topSold)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .orderBy(topSold.id.asc())
                .fetch();

        List<PairDTO<FileSystemItem, Long>> children = userList.stream()
                .map(item -> new PairDTO<>(item.getFileSystemItem(), item.getSalesCount()))
                .limit(numberOfResults)
                .toList();


        if (children.isEmpty() || children.size() <= numberOfResults) {
            return new PairDTO<>(children, null);
        }

        return new PairDTO<>(children, userList.get(userList.size() - 1).getId());
    }

}
