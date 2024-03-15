package com.andreidodu.europealibrary.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileMetaInfoDTO extends CommonDTO {
    protected Long id;
    protected String title;
    protected String description;
}
