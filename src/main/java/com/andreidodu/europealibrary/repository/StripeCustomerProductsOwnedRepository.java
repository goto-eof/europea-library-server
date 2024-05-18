package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.stripe.StripeCustomerProductsOwned;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.Optional;

public interface StripeCustomerProductsOwnedRepository extends TransactionalRepository<StripeCustomerProductsOwned, Long>, CustomStripeCustomerProductsOwnedRepository {
}
