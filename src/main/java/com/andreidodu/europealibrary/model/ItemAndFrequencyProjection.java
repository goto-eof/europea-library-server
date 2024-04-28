package com.andreidodu.europealibrary.model;

import com.andreidodu.europealibrary.model.common.Identificable;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemAndFrequencyProjection {

    private String name;
    private Long frequency;
    private Long nextCursor;

    @QueryProjection
    public ItemAndFrequencyProjection(String name, Long frequency, Long nextCursor) {
        this.name = name;
        this.frequency = frequency;
        this.nextCursor = nextCursor;
    }
}
