package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.common.Limitable;
import com.andreidodu.europealibrary.model.common.Identificable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CursoredServiceCommon {
    protected <T extends Identificable> Optional<Long> calculateNextId(List<T> itemsList, int limit) {
        if (itemsList.size() <= limit) {
            return Optional.empty();
        }
        return Optional.of(itemsList.get(limit).getId());
    }

    protected <T extends Identificable> List<T> limit(List<T> itemsList, int limit) {
        if (itemsList.size() <= limit) {
            return itemsList;
        }
        return itemsList.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }


}
