package com.andreidodu.europealibrary.util;

import com.andreidodu.europealibrary.dto.common.Limitable;

public class LimitUtil {
    public static <T extends Limitable> int calculateLimit(T commonCursoredRequestDTO, int defaultValue) {
        return commonCursoredRequestDTO.getLimit() == null ? defaultValue : commonCursoredRequestDTO.getLimit() > defaultValue ? defaultValue : commonCursoredRequestDTO.getLimit();
    }

    public static int calculateLimit(Integer limit, int defaultValue) {
        return limit == null ? defaultValue : limit > defaultValue ? defaultValue : limit;
    }
}
