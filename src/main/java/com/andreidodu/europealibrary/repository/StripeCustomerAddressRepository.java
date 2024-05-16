package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.model.stripe.StripeCustomerAddress;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

public interface StripeCustomerAddressRepository extends TransactionalRepository<StripeCustomerAddress, Long> {

}
