package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursorRequestDTO extends CursorCommonRequestDTO {
    private Long parentId;
}
