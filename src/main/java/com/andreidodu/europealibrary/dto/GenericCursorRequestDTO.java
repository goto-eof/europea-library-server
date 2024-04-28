package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.Limitable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericCursorRequestDTO<T> extends CursorCommonRequestDTO implements Limitable {
    private T parent;
}
