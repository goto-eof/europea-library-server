package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemAndFrequencyDTO {
    private String name;
    private Long frequency;
    private Long nextCursor;
}
