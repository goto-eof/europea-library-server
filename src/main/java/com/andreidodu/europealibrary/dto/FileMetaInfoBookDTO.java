package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.FileMetaInfoDTO;
import com.andreidodu.europealibrary.model.Category;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileMetaInfoBookDTO extends FileMetaInfoDTO {
    private String imageUrl;
    private String authors;
    private String note;
    private String isbn10;
    private String isbn13;
    private Integer numberOfPages;
    private String language;
    private String publisher;
    private String publishedDate;
    private Double averageRating;
    private Long ratingsCount;
    private Integer fileExtractionStatus;
    private Integer webRetrievementStatus;
    private List<Long> fileSystemItemIdList;
    private List<CategoryDTO> categoryList;
}
