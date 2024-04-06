package com.andreidodu.europealibrary.model;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileExtensionProjection {

    private String extension;
    private Long occurrence;
    private Long nextCursor;

    @QueryProjection
    public FileExtensionProjection(String extension, Long occurrence, Long nextCursor) {
        this.extension = extension;
        this.occurrence = occurrence;
        this.nextCursor = nextCursor;
    }
}
