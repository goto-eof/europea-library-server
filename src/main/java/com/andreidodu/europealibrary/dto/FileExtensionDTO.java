package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileExtensionDTO {
    private String extension;
    private Long occurrence;
    private Long nextCursor;
}
