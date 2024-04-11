package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.Limitable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchFileSystemItemRequestDTO implements Limitable {

    private String title;
    private String publisher;
    private String author;
    private String description;
    private Integer year;
    private String isbn;
    private Long nextCursor;
    private Integer limit;

}
