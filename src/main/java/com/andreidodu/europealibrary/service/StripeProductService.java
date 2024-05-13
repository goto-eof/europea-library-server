package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeProductDTO;
import com.stripe.exception.StripeException;

public interface StripeProductService {

    StripeProductDTO getByFileMetaInfoId(Long fileMetaInfoId);

    StripeProductDTO create(StripeProductDTO stripeProductDTO) throws StripeException;

    StripeProductDTO update(StripeProductDTO stripeProductDTO) throws StripeException;

    OperationStatusDTO delete(Long fileMetaInfoId);
}
