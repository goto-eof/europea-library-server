package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileExtensionDTO {
    private String name;
    private Long occurrence;
    private Long nextCursor;
}
