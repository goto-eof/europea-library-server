package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileSystemItemHighlightDTO {
    private Long id;
    private String title;
    private String imageUrl;
    private Double averageRating;
    private Long ratingsCount;
}
