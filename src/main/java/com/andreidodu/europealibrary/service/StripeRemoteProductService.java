package com.andreidodu.europealibrary.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Product;

public interface StripeRemoteProductService {
    Product createNewStripeProduct(String name, String description) throws StripeException;

    Product updateStripeProduct(String stripeProductId, String name, String description) throws StripeException;
}
