package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeProductDTO;
import com.andreidodu.europealibrary.resource.StripeProductAssemblerResource;
import com.andreidodu.europealibrary.service.StripeProductAssemblerService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StripeProductAssemblerResourceImpl implements StripeProductAssemblerResource {
    private final StripeProductAssemblerService stripeProductAssemblerService;

    @Override
    public ResponseEntity<StripePriceDTO> get(Long fileMetaInfoId) throws IOException, StripeException {
        return ResponseEntity.ok(this.stripeProductAssemblerService.getProductAssembly(fileMetaInfoId));
    }

    @Override
    public ResponseEntity<StripePriceDTO> create(StripePriceDTO stripePriceDTO) throws IOException, StripeException {
        return ResponseEntity.ok(this.stripeProductAssemblerService.createNewStripePriceAssembly(stripePriceDTO));
    }

    @Override
    public ResponseEntity<StripePriceDTO> update(StripePriceDTO stripePriceDTO) throws IOException, StripeException {
        return ResponseEntity.ok(this.stripeProductAssemblerService.updateStripeProductAssembly(stripePriceDTO));
    }
}
