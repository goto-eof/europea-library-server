package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.stripe.StripeCustomerDTO;
import com.stripe.exception.StripeException;

public interface StripeCustomerAssemblerService {
    StripeCustomerDTO get(String registrationEmail);

    StripeCustomerDTO create(String registeredUserEmail, StripeCustomerDTO stripeCustomerDTO) throws StripeException;

    StripeCustomerDTO update(String registeredUserEmail, StripeCustomerDTO stripeCustomerDTO) throws StripeException;
}
