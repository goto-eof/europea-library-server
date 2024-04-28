package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenericCursoredResponseDTO<T> {
    private T parent;
    private List<FileSystemItemDTO> childrenList;
    private Long nextCursor;
}
