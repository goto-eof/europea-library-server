package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataretriever.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataretriever.MetaInfoRetrieverStrategy;
import com.andreidodu.europealibrary.client.GoogleBooksClient;
import com.andreidodu.europealibrary.dto.GoogleBookResponseDTO;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleBookMetaInfoRetrieverStrategy implements MetaInfoRetrieverStrategy {

    public static final String IDENTIFIER_TYPE_ISBN_10 = "ISBN_10";
    public static final int MAX_RESULTS = 1;
    public static final String GOOGLE_QUERY_INTITLE = "intitle:";
    public static final String GOOGLE_QUERY_INAUTHOR = "inauthor:";
    public static final String GOOGLE_QUERY_INPUBLISHER = "inpublisher:";
    public static final String GOOGLE_QUERY_ISBN = "isbn:";
    @Value("${google.books.api_key}")
    private String googleBooksApiKey;

    private final GoogleBooksClient googleBooksClient;
    private final StringUtil stringUtil;

    @Override
    public boolean accept(FileSystemItem fileSystemItem) {
        return hasISBNOrTitleAuthorsOrPublisher(fileSystemItem);
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
    public void process(FileSystemItem fileSystemItem) {
        log.info("retrieving book information from google books....");
        GoogleBookResponseDTO googleBookResponse = retrieveGoogleBook(fileSystemItem);
        if (isEmptyResponse(googleBookResponse)) {
            log.info("book information not found for {}", fileSystemItem);
            return;
        }
        log.info("book information retrieved: {}", googleBookResponse);
        updateModel(fileSystemItem, googleBookResponse);

    }

    private void updateModel(FileSystemItem fileSystemItem, GoogleBookResponseDTO googleBookResponse) {
        GoogleBookResponseDTO.GoogleBookItemDTO.VolumeInfoDTO volumeInfo = googleBookResponse.getItems().getFirst().getVolumeInfo();
        FileMetaInfo fileMetaInfo = fileSystemItem.getFileMetaInfo();
        Optional.ofNullable(volumeInfo.getTitle()).ifPresent(fileMetaInfo::setTitle);
        Optional.ofNullable(volumeInfo.getDescription()).ifPresent(fileMetaInfo::setDescription);

        BookInfo bookInfo = fileMetaInfo.getBookInfo();
        Optional.ofNullable(volumeInfo.getAuthors())
                .ifPresent(authors -> bookInfo.setAuthors(String.join(",", authors)));
        Optional.ofNullable(volumeInfo.getLanguage()).ifPresent(bookInfo::setLanguage);
        Optional.ofNullable(volumeInfo.getPublisher()).ifPresent(bookInfo::setPublisher);
        bookInfo.setAverageRating(volumeInfo.getAverageRating());
        bookInfo.setRatingsCount(volumeInfo.getRatingsCount());
        bookInfo.setIsbn10(extractValue(volumeInfo.getIndustryIdentifiers(), IDENTIFIER_TYPE_ISBN_10));
        Optional.ofNullable(volumeInfo.getPublishedDate()).ifPresent(bookInfo::setPublishedDate);
        Optional.ofNullable(volumeInfo.getImageLinks())
                .flatMap(imageLinks -> Optional.ofNullable(imageLinks.getThumbnail()))
                .ifPresent(bookInfo::setImageUrl);
    }

    private static boolean isEmptyResponse(GoogleBookResponseDTO googleBookResponse) {
        return googleBookResponse == null || googleBookResponse.getItems() == null || googleBookResponse.getItems().isEmpty();
    }

    private GoogleBookResponseDTO retrieveGoogleBook(FileSystemItem fileSystemItem) {
        GoogleBookResponseDTO googleBookResponse;
        try {
            if (hasISBN13(fileSystemItem)) {
                googleBookResponse = this.googleBooksClient.retrieveMetaInfo(calculateQueryISBN13(fileSystemItem), MAX_RESULTS, googleBooksApiKey);
            } else {
                googleBookResponse = this.googleBooksClient.retrieveMetaInfo(calculateQueryTitleAuthorPublisher(fileSystemItem), MAX_RESULTS, googleBooksApiKey);
            }
        } catch (RuntimeException e) {
            log.error("Error while trying to contact google books api: {}", e.getMessage());
            return null;
        }
        return googleBookResponse;
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