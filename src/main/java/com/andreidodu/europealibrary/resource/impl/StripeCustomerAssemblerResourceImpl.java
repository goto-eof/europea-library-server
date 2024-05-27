package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.stripe.StripeCustomerDTO;
import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.andreidodu.europealibrary.resource.StripeCustomerAssemblerResource;
import com.andreidodu.europealibrary.resource.StripeProductAssemblerResource;
import com.andreidodu.europealibrary.service.StripeCustomerAssemblerService;
import com.andreidodu.europealibrary.service.StripeProductAssemblerService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StripeCustomerAssemblerResourceImpl implements StripeCustomerAssemblerResource {
    private final StripeCustomerAssemblerService stripeCustomerAssemblerService;

    @Override
    public ResponseEntity<StripeCustomerDTO> get(Authentication authentication) throws IOException, StripeException {
        return ResponseEntity.ok(this.stripeCustomerAssemblerService.get(authentication.getName()));
    }

    @Override
    public ResponseEntity<StripeCustomerDTO> create(Authentication authentication, StripeCustomerDTO stripeCustomerDTO) throws IOException, StripeException {
        return ResponseEntity.ok(this.stripeCustomerAssemblerService.create(authentication.getName(), stripeCustomerDTO));
    }

    @Override
    public ResponseEntity<StripeCustomerDTO> update(Authentication authentication, StripeCustomerDTO stripeCustomerDTO) throws IOException, StripeException {
        return ResponseEntity.ok(this.stripeCustomerAssemblerService.update(authentication.getName(), stripeCustomerDTO));
    }
}
