package com.andreidodu.europealibrary.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.param.PriceCreateParams;

public interface StripeRemotePriceService {
    Price createNewStripePrice(String stripProductName,
                               Long amount,
                               PriceCreateParams.Recurring.Interval interval
    ) throws StripeException;

    Price deactivate(String stripePriceId) throws StripeException;
}
