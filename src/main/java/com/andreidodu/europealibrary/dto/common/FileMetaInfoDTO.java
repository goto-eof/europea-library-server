package com.andreidodu.europealibrary.dto.common;

import com.andreidodu.europealibrary.dto.TagDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileMetaInfoDTO extends CommonDTO {
    protected Long id;
    protected String title;
    protected String description;
    private List<TagDTO> tagList;
}
