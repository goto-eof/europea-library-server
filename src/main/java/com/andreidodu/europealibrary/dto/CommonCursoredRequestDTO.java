package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.Limitable;
import com.andreidodu.europealibrary.enums.OrderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonCursoredRequestDTO implements Limitable {
    private Long nextCursor;
    private Integer limit;
    private OrderEnum order;

    public CommonCursoredRequestDTO(Long nextCursor, Integer limit) {
        this.nextCursor = nextCursor;
        this.limit = limit;
    }
}
