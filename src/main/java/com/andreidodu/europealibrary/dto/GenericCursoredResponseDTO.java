package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenericCursoredResponseDTO<T, U> {
    private T parent;
    private List<U> childrenList;
    private Long nextCursor;
}
