package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.stripe.StripePurchaseSession;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

public interface StripePurchaseSessionRepository extends TransactionalRepository<StripePurchaseSession, Long> {
}
