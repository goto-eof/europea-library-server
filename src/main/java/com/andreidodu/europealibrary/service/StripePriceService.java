package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.stripe.exception.StripeException;

public interface StripePriceService {
    StripePriceDTO getStripePrice(Long fileMetaInfoId);

    StripePriceDTO createStripePrice(StripePriceDTO stripePriceDTO) throws StripeException;

    StripePriceDTO updateStripePrice(StripePriceDTO stripePriceDTO) throws StripeException;

    OperationStatusDTO deleteStripePrice(Long fileMetaInfoId);
}
