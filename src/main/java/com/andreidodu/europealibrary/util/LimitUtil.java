package com.andreidodu.europealibrary.util;

import com.andreidodu.europealibrary.dto.common.Limitable;

public class LimitUtil {
    public static <T extends Limitable> int calculateLimit(T commonCursoredRequestDTO, int defaultValue) {
        return commonCursoredRequestDTO.getLimit() == null ? defaultValue : commonCursoredRequestDTO.getLimit() > defaultValue ? defaultValue : commonCursoredRequestDTO.getLimit();
    }
}
