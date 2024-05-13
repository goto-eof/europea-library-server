package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.stripe.StripeProductDTO;
import com.stripe.exception.StripeException;

public interface StripeProductAssemblerService {
    StripeProductDTO getProductAssembly(Long fileMetaInfoId);

    StripeProductDTO createNewStripeProductAssembly(StripeProductDTO stripeProductDTO) throws StripeException;

    StripeProductDTO updateStripeProductAssembly(StripeProductDTO stripeProductDTO) throws StripeException;
}
