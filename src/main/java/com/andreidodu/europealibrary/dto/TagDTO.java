package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDTO extends CommonDTO {
    private Long id;
    private String name;
}
