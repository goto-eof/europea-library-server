package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursorRequestDTO {
    Long parentId;
    Long nextCursor;
    Integer limit;
}
