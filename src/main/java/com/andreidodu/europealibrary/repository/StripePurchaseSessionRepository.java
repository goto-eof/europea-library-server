package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.stripe.StripePurchaseSession;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;
import com.andreidodu.europealibrary.service.ApplicationSettingsService;

import java.util.Optional;

public interface StripePurchaseSessionRepository extends TransactionalRepository<StripePurchaseSession, Long> {
    Optional<StripePurchaseSession> findByStripePaymentIntentId(String paymentIntentId);
}
