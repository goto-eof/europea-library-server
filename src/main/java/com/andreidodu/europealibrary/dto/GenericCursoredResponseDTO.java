package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenericCursoredResponseDTO<T, U> extends CommonGenericCursoredResponseDTO<U> {
    private T parent;
}
