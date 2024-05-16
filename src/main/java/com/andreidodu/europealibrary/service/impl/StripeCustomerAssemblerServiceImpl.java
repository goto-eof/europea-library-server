package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.stripe.StripeCustomerDTO;
import com.andreidodu.europealibrary.mapper.stripe.StripeCustomerAddressMapper;
import com.andreidodu.europealibrary.mapper.stripe.StripeCustomerMapper;
import com.andreidodu.europealibrary.model.security.User;
import com.andreidodu.europealibrary.model.stripe.StripeCustomer;
import com.andreidodu.europealibrary.model.stripe.StripeCustomerAddress;
import com.andreidodu.europealibrary.repository.StripeCustomerAddressRepository;
import com.andreidodu.europealibrary.repository.StripeCustomerRepository;
import com.andreidodu.europealibrary.repository.security.UserRepository;
import com.andreidodu.europealibrary.service.StripeCustomerAssemblerService;
import com.andreidodu.europealibrary.service.StripeRemoteCustomerService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripeCustomerAssemblerServiceImpl implements StripeCustomerAssemblerService {
    private final StripeCustomerAddressRepository stripeCustomerAddressRepository;
    private final StripeCustomerAddressMapper stripeCustomerAddressMapper;
    private final StripeRemoteCustomerService stripeRemoteCustomerService;
    private final StripeCustomerRepository stripeCustomerRepository;
    private final StripeCustomerMapper stripeCustomerMapper;
    private final UserRepository userRepository;

    @Override
    public StripeCustomerDTO get(String registrationEmail) {
        StripeCustomer stripeCustomer = this.stripeCustomerRepository.findByUser_username(registrationEmail).orElse(new StripeCustomer());
        return this.stripeCustomerMapper.toDTO(stripeCustomer);
    }

    @Override
    public StripeCustomerDTO create(String registeredUserEmail, StripeCustomerDTO stripeCustomerDTO) throws StripeException {
        User user = this.userRepository.findByUsername(registeredUserEmail).orElseThrow();


        StripeCustomer stripeCustomer = this.stripeCustomerMapper.toModelWithoutAddress(stripeCustomerDTO);
        stripeCustomer.setUser(user);
        stripeCustomer = this.stripeCustomerRepository.save(stripeCustomer);


        StripeCustomerAddress stripeCustomerAddress = this.stripeCustomerAddressMapper.toModel(stripeCustomerDTO.getCurrentStripeCustomerAddress());
        stripeCustomerAddress.setStripeCustomer(stripeCustomer);
        stripeCustomerAddress = this.stripeCustomerAddressRepository.save(stripeCustomerAddress);
        stripeCustomerAddress.setStripeCustomer(stripeCustomer);

        stripeCustomer.setCurrentStripeCustomerAddress(stripeCustomerAddress);


        Customer customer = this.stripeRemoteCustomerService.create(stripeCustomerDTO);
        stripeCustomer.setStripeCustomerId(customer.getId());

        stripeCustomer = this.stripeCustomerRepository.save(stripeCustomer);

        return this.stripeCustomerMapper.toDTO(stripeCustomer);
    }

    @Override
    public StripeCustomerDTO update(String registeredUserEmail, StripeCustomerDTO stripeCustomerDTO) throws StripeException {
        User user = this.userRepository.findByUsername(registeredUserEmail).orElseThrow();
        StripeCustomer stripeCustomer = user.getStripeCustomer();

        StripeCustomerAddress address = stripeCustomer.getCurrentStripeCustomerAddress();

        this.stripeCustomerMapper.map(stripeCustomer, stripeCustomerDTO);
        this.stripeCustomerAddressMapper.map(address, stripeCustomerDTO.getCurrentStripeCustomerAddress());

        Customer customer = this.stripeRemoteCustomerService.update(stripeCustomer.getStripeCustomerId(), stripeCustomerDTO);

        this.stripeCustomerAddressRepository.save(address);
        return this.stripeCustomerMapper.toDTO(this.stripeCustomerRepository.save(stripeCustomer));
    }

}
