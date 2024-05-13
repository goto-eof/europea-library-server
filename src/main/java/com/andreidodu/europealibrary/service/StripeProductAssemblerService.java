package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.stripe.exception.StripeException;

public interface StripeProductAssemblerService {
    StripePriceDTO getProductAssembly(Long fileMetaInfoId);

    StripePriceDTO createNewStripePriceAssembly(StripePriceDTO stripePriceDTO) throws StripeException;

    StripePriceDTO updateStripeProductAssembly(StripePriceDTO stripePriceDTO) throws StripeException;
}
