package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.stripe.StripePrice;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.Optional;

public interface StripePriceRepository extends TransactionalRepository<StripePrice, Long> {
    Optional<StripePrice> findByStripeProduct_FileMetaInfo_id(Long fileMetaInfoId);
}
