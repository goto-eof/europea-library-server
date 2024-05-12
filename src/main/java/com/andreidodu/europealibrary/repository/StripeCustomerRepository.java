package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.stripe.StripeCustomer;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.Optional;

public interface StripeCustomerRepository extends TransactionalRepository<StripeCustomer, Long> {
    Optional<StripeCustomer> findByUser_email(String email);

    boolean existsByUser_email(String email);

    Optional<StripeCustomer> findByUser_username(String username);

    boolean existsByUser_username(String username);
}
