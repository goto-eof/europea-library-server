package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.stripe.StripeProduct;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.Optional;

public interface StripeProductRepository extends TransactionalRepository<StripeProduct, Long> {
    Optional<StripeProduct> findByFileMetaInfo_id(Long fileMetaInfoId);

    boolean existsByFileMetaInfo_id(Long fileMetaInfoId);
}
