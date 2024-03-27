package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursorRequestDTO {
    private Long parentId;
    private Long nextCursor;
    private Integer limit;
}
