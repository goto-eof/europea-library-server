package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.stripe.StripeProduct;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.Optional;

public interface StripeProductRepository extends TransactionalRepository<StripeProduct, Long> {
    Optional<StripeProduct> findByStripeProduct_fileMetaInfo_id(Long fileMetaInfoId);
}
