package com.andreidodu.europealibrary.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TransactionalRepository<T, V> extends JpaRepository<T, V>, QuerydslPredicateExecutor<T> {
}

