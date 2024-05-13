package com.andreidodu.europealibrary.service;

import com.stripe.exception.StripeException;

public interface StripeRemoteProductService {
    String createNewStripeProduct(String name, String description) throws StripeException;
}
