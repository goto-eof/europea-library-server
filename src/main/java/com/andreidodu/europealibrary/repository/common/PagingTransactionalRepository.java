package com.andreidodu.europealibrary.repository.common;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface PagingTransactionalRepository<T, V> extends PagingAndSortingRepository<T, V> {
}

