package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursorCommonRequestDTO {
    private Long nextCursor;
    private Integer limit;
}
