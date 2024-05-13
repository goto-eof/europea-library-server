package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.model.common.Identifiable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CursoredServiceCommon {

    protected <T extends Identifiable> Optional<Long> calculateNextId(List<T> itemsList, Integer limit, int defaultLimit) {
        if (limit == null) {
            limit = defaultLimit;
        }
        if (limit > defaultLimit) {
            limit = defaultLimit;
        }
        if (itemsList.size() <= limit) {
            return Optional.empty();
        }
        return Optional.of(itemsList.get(limit).getId());
    }

    protected <T extends Identifiable> List<T> limit(List<T> itemsList, Integer limit, int defaultLimit) {
        if (limit == null) {
            limit = defaultLimit;
        }
        if (limit > defaultLimit) {
            limit = defaultLimit;
        }
        if (itemsList.size() <= limit) {
            return itemsList;
        }
        return itemsList.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

}
