package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.stripe.StripeCustomerAddressDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCustomerDTO;
import com.andreidodu.europealibrary.service.StripeRemoteCustomerService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripeRemoteCustomerServiceImpl implements StripeRemoteCustomerService {


    @Override
    public Customer create(StripeCustomerDTO stripeCustomerDTO) throws StripeException {
        StripeCustomerAddressDTO stripeCustomerAddressDTO = stripeCustomerDTO.getCurrentStripeCustomerAddress();
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(stripeCustomerDTO.getFirstName() + " " + stripeCustomerDTO.getLastName())
                        .setEmail(stripeCustomerDTO.getEmail())
                        .setAddress(CustomerCreateParams.Address.builder()
                                .setCity(stripeCustomerAddressDTO.getCity())
                                .setCountry(stripeCustomerAddressDTO.getCountry())
                                .setLine1(stripeCustomerAddressDTO.getLine1())
                                .setLine2(stripeCustomerAddressDTO.getLine2())
                                .setPostalCode(stripeCustomerAddressDTO.getPostalCode())
                                .setState(stripeCustomerAddressDTO.getState())
                                .build())
                        .build();
        return Customer.create(params);
    }

    @Override
    public Customer update(String stripeCustomerId, StripeCustomerDTO stripeCustomerDTO) throws StripeException {
        Customer resource = Customer.retrieve(stripeCustomerId);
        StripeCustomerAddressDTO stripeCustomerAddressDTO = stripeCustomerDTO.getCurrentStripeCustomerAddress();
        CustomerUpdateParams params =
                CustomerUpdateParams.builder()
                        .setName(stripeCustomerDTO.getFirstName() + " " + stripeCustomerDTO.getLastName())
                        .setEmail(stripeCustomerDTO.getEmail())
                        .setAddress(CustomerUpdateParams.Address.builder()
                                .setCity(stripeCustomerAddressDTO.getCity())
                                .setCountry(stripeCustomerAddressDTO.getCountry())
                                .setLine1(stripeCustomerAddressDTO.getLine1())
                                .setLine2(stripeCustomerAddressDTO.getLine2())
                                .setPostalCode(stripeCustomerAddressDTO.getPostalCode())
                                .setState(stripeCustomerAddressDTO.getState())
                                .build())
                        .build();
        return resource.update(params);
    }


}
