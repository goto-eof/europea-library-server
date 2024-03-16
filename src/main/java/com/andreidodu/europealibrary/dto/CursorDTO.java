package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CursorDTO<T> {
    List<T> items;
    Long nextCursor;
}
