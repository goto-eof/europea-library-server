package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CursorDTO<T> {
    private List<T> items;
    private Long nextCursor;
}
