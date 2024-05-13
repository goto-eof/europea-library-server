package com.andreidodu.europealibrary.service;

import com.stripe.exception.StripeException;
import com.stripe.param.PriceCreateParams;

public interface StripeRemotePriceService {
    String createNewStripePrice(String stripProductName,
                                Long amount,
                                PriceCreateParams.Recurring.Interval interval
    ) throws StripeException;
}
