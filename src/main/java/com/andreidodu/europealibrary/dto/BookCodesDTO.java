package com.andreidodu.europealibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookCodesDTO<A, B> {
    private A isbn10;
    private B isbn13;
}
