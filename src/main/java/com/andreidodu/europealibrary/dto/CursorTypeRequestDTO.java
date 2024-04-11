package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.Limitable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursorTypeRequestDTO extends CursorCommonRequestDTO implements Limitable {
    private String extension;

}
