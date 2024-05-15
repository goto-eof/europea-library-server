package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookInfoDTO {
    private Long id;
    private String imageUrl;
    private String authors;
    private String note;
    private String isbn10;
    private String isbn13;
    private String publisher;
    private String language;
    private Integer numberOfPages;
    private String publishedDate;
    private Double averageRating;
    private Long ratingsCount;
    private Integer fileExtractionStatus;
    private Integer webRetrievementStatus;
    private Integer manualLock;
    private Integer recordStatus;
    private Boolean isCorrupted;
    private List<CategoryDTO> categoryList;
}
