package com.andreidodu.europealibrary.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationSettingsDTO {
    private Long id;
    private Boolean applicationLock;

    private Long featuredFileSystemItemId;
    private Boolean customDescriptionEnabled;
    private Boolean protectedDownloadsEnabled;
    private Boolean featuredBooksWidgetEnabled;
    private Boolean featuredBookWidgetEnabled;
    private Boolean newBooksWidgetEnabled;
    private Boolean popularBooksWidgetEnabled;
}
