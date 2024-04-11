package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResultDTO<Q, C> {
    Q query;
    private List<C> childrenList;
    private Long nextCursor;
}
