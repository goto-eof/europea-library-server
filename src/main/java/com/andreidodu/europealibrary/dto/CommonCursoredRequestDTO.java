package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonCursoredRequestDTO {
    private Long nextCursor;
    private Integer limit;
}
