package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.Limitable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursorRequestDTO extends CursorCommonRequestDTO implements Limitable {
    private Long parentId;
}
