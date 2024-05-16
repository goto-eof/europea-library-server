package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.stripe.StripePrice;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StripePriceRepository extends TransactionalRepository<StripePrice, Long> {
    Optional<StripePrice> findByStripeProduct_FileMetaInfo_id(Long fileMetaInfoId);

    @Modifying
    @Query("update StripePrice sp set sp.archived = :isArchived where sp.id = :id")
    void archive(boolean isArchived, Long id);

    Optional<StripePrice> findByStripeProduct_FileMetaInfo_idAndArchivedNot(Long fileMetaInfoId, boolean b);

    Optional<StripePrice> findByStripeProduct_FileMetaInfo_idAndArchivedIsNull(Long fileMetaInfoId);
}
