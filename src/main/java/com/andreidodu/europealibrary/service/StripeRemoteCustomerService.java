package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.stripe.StripeCustomerDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

public interface StripeRemoteCustomerService {

    Customer create(StripeCustomerDTO stripeCustomerDTO) throws StripeException;

    Customer update(String stripeCustomerId, StripeCustomerDTO stripeCustomerDTO) throws StripeException;
}
