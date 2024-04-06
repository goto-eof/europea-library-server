package com.andreidodu.europealibrary.batch.indexer.step.externalapi.dataretriever.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.common.StepUtil;
import com.andreidodu.europealibrary.batch.indexer.step.externalapi.dataretriever.MetaInfoRetrieverStrategy;
import com.andreidodu.europealibrary.batch.indexer.enums.ApiStatusEnum;
import com.andreidodu.europealibrary.batch.indexer.enums.WebRetrievementStatusEnum;
import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy.TagUtil;
import com.andreidodu.europealibrary.client.GoogleBooksClient;
import com.andreidodu.europealibrary.constants.DataPropertiesConst;
import com.andreidodu.europealibrary.dto.ApiResponseDTO;
import com.andreidodu.europealibrary.dto.GoogleBookResponseDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.model.*;
import com.andreidodu.europealibrary.repository.BookInfoRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class GoogleBookMetaInfoRetrieverStrategy implements MetaInfoRetrieverStrategy {
    private static final String STRATEGY_NAME = "google-book-meta-info-retriever-strategy";
    public static final String IDENTIFIER_TYPE_ISBN_10 = "ISBN_10";
    public static final int MAX_RESULTS = 1;
    public static final String GOOGLE_QUERY_INTITLE = "intitle:";
    public static final String GOOGLE_QUERY_INAUTHOR = "inauthor:";
    public static final String GOOGLE_QUERY_INPUBLISHER = "inpublisher:";
    public static final String GOOGLE_QUERY_ISBN = "isbn:";
    @Value("${google.books.api_key}")
    private String googleBooksApiKey;

    private final GoogleBooksClient googleBooksClient;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final CategoryUtil categoryUtil;
    private final TagUtil tagUtil;
    private final StepUtil stepUtil;
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.force-load-meta-info-from-web}")
    private boolean forceLoadMetaInfoFromWeb;

    private final BookInfoRepository bookInfoRepository;

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public boolean accept(FileSystemItem fileSystemItem) {
        return forceLoadMetaInfoFromWeb || (StringUtil.isNotEmpty(googleBooksApiKey) && wasNotAlreadyRetrievedFromWeb(fileSystemItem)) && hasISBNOrTitleAuthorsOrPublisher(fileSystemItem);
    }

    private boolean wasNotAlreadyRetrievedFromWeb(FileSystemItem fileSystemItem) {
        if (fileSystemItem == null || fileSystemItem.getFileMetaInfo() == null || fileSystemItem.getFileMetaInfo().getBookInfo() == null) {
            return false;
        }
        BookInfo bookInfo = fileSystemItem.getFileMetaInfo().getBookInfo();
        List<Integer> successStatuses = List.of(WebRetrievementStatusEnum.SUCCESS.getStatus(), WebRetrievementStatusEnum.SUCCESS_EMPTY.getStatus());
        Integer webRetrieveStatus = bookInfo.getWebRetrievementStatus();
        return webRetrieveStatus == null || !successStatuses.contains(webRetrieveStatus);
    }

    private static boolean hasISBNOrTitleAuthorsOrPublisher(FileSystemItem fileSystemItem) {
        return fileSystemItem != null &&
                fileSystemItem.getFileMetaInfo() != null &&
                fileSystemItem.getFileMetaInfo().getBookInfo() != null &&
                hasISBN13(fileSystemItem) ||
                fileSystemItem != null &&
                        fileSystemItem.getFileMetaInfo() != null &&
                        fileSystemItem.getFileMetaInfo().getTitle() != null &&
                        fileSystemItem.getFileMetaInfo().getBookInfo() != null &&
                        (StringUtil.isNotEmpty(fileSystemItem.getFileMetaInfo().getBookInfo().getAuthors()) ||
                                StringUtil.isNotEmpty(fileSystemItem.getFileMetaInfo().getBookInfo().getPublisher()));
    }

    private static boolean hasISBN13(FileSystemItem fileSystemItem) {
        return StringUtil.isNotEmpty(fileSystemItem.getFileMetaInfo().getBookInfo().getIsbn13());
    }

    @Override
    public ApiResponseDTO<FileMetaInfo> process(FileSystemItem fileSystemItem) {
        log.info("applying strategy: {}", getStrategyName());
        log.info("retrieving book information from google books....");
        GoogleBookResponseDTO googleBookResponse;
        try {
            googleBookResponse = retrieveGoogleBook(fileSystemItem);
        } catch (Exception e) {
            log.error("google books api throw an error: {}", e.getMessage());
            ApiResponseDTO<FileMetaInfo> apiResponseDTO = new ApiResponseDTO<FileMetaInfo>();
            apiResponseDTO.setStatus(ApiStatusEnum.FATAL_ERROR);
            return apiResponseDTO;
        }
        if (isEmptyResponse(googleBookResponse)) {
            log.info("book information not found for {}", fileSystemItem);
            fileSystemItem.getFileMetaInfo().getBookInfo().setWebRetrievementStatus(WebRetrievementStatusEnum.SUCCESS_EMPTY.getStatus());
            ApiResponseDTO<FileMetaInfo> apiResponseDTO = new ApiResponseDTO<FileMetaInfo>();
            apiResponseDTO.setStatus(ApiStatusEnum.SUCCESS_EMPTY_RESPONSE);
            return apiResponseDTO;
        }
        log.info("book information retrieved: {}", googleBookResponse);
        updateModel(fileSystemItem, googleBookResponse);
        fileSystemItem.getFileMetaInfo().getBookInfo().setWebRetrievementStatus(WebRetrievementStatusEnum.SUCCESS.getStatus());
        ApiResponseDTO<FileMetaInfo> apiResponseDTO = new ApiResponseDTO<FileMetaInfo>();
        apiResponseDTO.setEntity(fileSystemItem.getFileMetaInfo());
        apiResponseDTO.setStatus(ApiStatusEnum.SUCCESS);
        return apiResponseDTO;
    }

    private void updateModel(FileSystemItem fileSystemItem, GoogleBookResponseDTO googleBookResponse) {
        GoogleBookResponseDTO.GoogleBookItemDTO.VolumeInfoDTO volumeInfo = googleBookResponse.getItems().stream().findFirst().get().getVolumeInfo();
        FileMetaInfo fileMetaInfoOld = fileSystemItem.getFileMetaInfo();
        FileMetaInfo fileMetaInfo = fileMetaInfoOld == null ? new FileMetaInfo() : fileMetaInfoOld;
        Optional.ofNullable(volumeInfo.getTitle()).ifPresent(title -> fileMetaInfo.setTitle(StringUtil.cleanAndTrimToNullSubstring(title, DataPropertiesConst.FILE_META_INFO_TITLE_MAX_LENGTH)));
        Optional.ofNullable(volumeInfo.getDescription()).ifPresent(description -> {
            String descriptionNew = description.substring(0, Math.min(description.length(), DataPropertiesConst.FILE_META_INFO_DESCRIPTION_MAX_LENGTH));
            fileMetaInfo.setDescription(descriptionNew);
        });

        BookInfo oldBookInfo = fileMetaInfo.getBookInfo();
        BookInfo bookInfo = oldBookInfo == null ? new BookInfo() : oldBookInfo;
        BookInfo finalBookInfo = bookInfo;
        Optional.ofNullable(volumeInfo.getAuthors())
                .ifPresent(authors -> finalBookInfo.setAuthors(String.join(",", authors)));
        Optional.ofNullable(StringUtil.cleanAndTrimToNullLowerCaseSubstring(volumeInfo.getLanguage(), DataPropertiesConst.BOOK_INFO_LANGUAGE_MAX_LENGTH))
                .ifPresent(bookInfo::setLanguage);
        Optional.ofNullable(StringUtil.cleanAndTrimToNullSubstring(volumeInfo.getPublisher(), DataPropertiesConst.BOOK_INFO_PUBLISHER_MAX_LENGTH))
                .ifPresent(bookInfo::setPublisher);
        bookInfo.setAverageRating(volumeInfo.getAverageRating());
        bookInfo.setRatingsCount(volumeInfo.getRatingsCount());
        bookInfo.setIsbn10(extractValue(volumeInfo.getIndustryIdentifiers(), IDENTIFIER_TYPE_ISBN_10));
        Optional.ofNullable(volumeInfo.getPublishedDate()).ifPresent(bookInfo::setPublishedDate);
        Optional.ofNullable(volumeInfo.getImageLinks())
                .flatMap(imageLinks -> Optional.ofNullable(imageLinks.getThumbnail()))
                .ifPresent(bookInfo::setImageUrl);

        FileMetaInfo savedFileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);


        Set<String> categoryNames = Optional.ofNullable(volumeInfo.getCategories())
                .map(this.stepUtil::explodeInUniqueItems)
                .orElse(new HashSet<>());

        savedFileMetaInfo = this.createAndAssociateTags(categoryNames, savedFileMetaInfo);
        bookInfo = savedFileMetaInfo.getBookInfo();
        bookInfo = this.createAndAssociateCategoriesIfNecessary(categoryNames, bookInfo);
        savedFileMetaInfo = this.fileMetaInfoRepository.save(savedFileMetaInfo);

    }


    private FileMetaInfo createAndAssociateTags(Set<String> tagsSet, FileMetaInfo savedFileMetaInfo) {
        try {
            List<String> tags = new ArrayList<>(tagsSet);
            Set<Tag> explodedTags = this.stepUtil.createOrLoadItems(tags);
            savedFileMetaInfo = this.stepUtil.associateTags(savedFileMetaInfo, explodedTags);
        } catch (Exception e) {
            log.error("something went wrong with tag creation/association: {}", e.getMessage());
        }
        return savedFileMetaInfo;
    }

    private BookInfo createAndAssociateCategoriesIfNecessary(Set<String> categoriesSet, BookInfo bookInfo) {
        try {
            List<String> categories = new ArrayList<>(categoriesSet);
            Set<Category> explodedTags = this.stepUtil.createOrLoadCategories(categories);
            bookInfo = this.stepUtil.associateCategories(bookInfo, explodedTags);
            return bookInfo;
        } catch (Exception e) {
            log.error("something went wrong with tag creation/association: {}", e.getMessage());
        }
        return bookInfo;
    }

    private static boolean isEmptyResponse(GoogleBookResponseDTO googleBookResponse) {
        return googleBookResponse == null || googleBookResponse.getItems() == null || googleBookResponse.getItems().isEmpty();
    }

    private GoogleBookResponseDTO retrieveGoogleBook(FileSystemItem fileSystemItem) {
        GoogleBookResponseDTO googleBookResponse;
        try {
            googleBookResponse = retrieveBookInfoFromGoogleBooks(fileSystemItem);
        } catch (RuntimeException e) {
            log.error("Error while trying to contact google books api: {}", e.getMessage());
            throw new ApplicationException("Google Books API throw an error: " + e.getMessage(), e);
        }
        return googleBookResponse;
    }

    private GoogleBookResponseDTO retrieveBookInfoFromGoogleBooks(FileSystemItem fileSystemItem) {
        if (hasISBN13(fileSystemItem)) {
            return this.googleBooksClient.retrieveMetaInfo(calculateQueryISBN13(fileSystemItem), MAX_RESULTS, googleBooksApiKey);
        }
        return this.googleBooksClient.retrieveMetaInfo(calculateQueryTitleAuthorPublisher(fileSystemItem), MAX_RESULTS, googleBooksApiKey);
    }

    private static String calculateQueryISBN13(FileSystemItem fileSystemItem) {
        return GOOGLE_QUERY_ISBN + fileSystemItem.getFileMetaInfo().getBookInfo().getIsbn13();
    }

    private String calculateQueryTitleAuthorPublisher(FileSystemItem fileSystemItem) {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtil.isNotEmpty(fileSystemItem.getFileMetaInfo().getTitle())) {
            stringBuilder.append(" ").append(GOOGLE_QUERY_INTITLE).append(fileSystemItem.getFileMetaInfo().getTitle());
        }
        if (StringUtil.isNotEmpty(fileSystemItem.getFileMetaInfo().getBookInfo().getAuthors())) {
            stringBuilder.append(" ").append(GOOGLE_QUERY_INAUTHOR).append(fileSystemItem.getFileMetaInfo().getBookInfo().getAuthors());
        }
        if (StringUtil.isNotEmpty(fileSystemItem.getFileMetaInfo().getBookInfo().getPublisher())) {
            stringBuilder.append(" ").append(GOOGLE_QUERY_INPUBLISHER).append(fileSystemItem.getFileMetaInfo().getBookInfo().getPublisher());
        }
        return stringBuilder.toString();
    }

    private String extractValue(List<GoogleBookResponseDTO.GoogleBookItemDTO.VolumeInfoDTO.IndustryIdentifierDTO> industryIdentifiers, String type) {
        Optional<List<GoogleBookResponseDTO.GoogleBookItemDTO.VolumeInfoDTO.IndustryIdentifierDTO>> industryIdentifierDTOOptional = Optional.ofNullable(industryIdentifiers);
        return industryIdentifierDTOOptional.flatMap(industryIdentifierDTOS -> industryIdentifierDTOS
                        .stream()
                        .filter(item -> type.equals(item.getType()))
                        .map(GoogleBookResponseDTO.GoogleBookItemDTO.VolumeInfoDTO.IndustryIdentifierDTO::getIdentifier)
                        .findFirst())
                .orElse(null);
    }
}
