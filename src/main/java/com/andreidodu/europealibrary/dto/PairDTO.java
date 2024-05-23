package com.andreidodu.europealibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PairDTO<T, V> {
    private T val1;
    private V val2;
}
