package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommonGenericCursoredResponseDTO<U> {
    private List<U> childrenList;
    private Long nextCursor;
}
