package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.AuthConst;
import com.andreidodu.europealibrary.dto.PaginatedExplorerOptions;
import com.andreidodu.europealibrary.enums.OrderEnum;
import com.andreidodu.europealibrary.model.common.Identifiable;
import jakarta.persistence.criteria.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public abstract class CursoredServiceCommon {

    protected <T extends Identifiable> Optional<Long> calculateNextId(List<T> itemsList, Integer limit, int defaultLimit, OrderEnum order) {
        if (limit == null || limit > defaultLimit) {
            limit = defaultLimit;
        }

        if (itemsList.size() <= limit) {
            return Optional.empty();
        }

        long nextId = calculateNextId(itemsList, order);

        return Optional.of(nextId);
    }

    private static <T extends Identifiable> long calculateNextId(List<T> itemsList, OrderEnum order) {
        LongStream idStream = itemsList.stream()
                .map(Identifiable::getId)
                .mapToLong(Long::longValue);
        return order == OrderEnum.ASC ? idStream
                .max()
                .orElseThrow() :
                idStream
                        .min()
                        .orElseThrow();
    }

    protected <T extends Identifiable> List<T> limit(List<T> itemsList, Integer limit, int defaultLimit, OrderEnum order) {
        if (limit == null || limit > defaultLimit) {
            limit = defaultLimit;
        }

        if (itemsList.size() <= limit) {
            return itemsList;
        }

        long nextId = calculateNextId(itemsList, order);

        return itemsList.stream()
                .filter(item -> !item.getId().equals(nextId))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public static PaginatedExplorerOptions buildPaginatedExplorerOptions(Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(auth -> {
                    PaginatedExplorerOptions paginatedExplorerOptions = new PaginatedExplorerOptions();
                    boolean isAdministrator = auth.getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .anyMatch(AuthConst.AUTHORITY_ADMINISTRATOR::equalsIgnoreCase);
                    paginatedExplorerOptions.setAdministratorFlag(isAdministrator);
                    paginatedExplorerOptions.setUsername(auth.getName());
                    return paginatedExplorerOptions;
                })
                .orElse(new PaginatedExplorerOptions());
    }

}
