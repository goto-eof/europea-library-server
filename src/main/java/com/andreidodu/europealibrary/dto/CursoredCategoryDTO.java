package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CursoredCategoryDTO {
    private CategoryDTO categoryDTO;
    private List<FileSystemItemDTO> childrenList;
    private Long nextCursor;
}
