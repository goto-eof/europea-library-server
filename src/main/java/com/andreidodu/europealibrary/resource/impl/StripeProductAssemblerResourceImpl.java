package com.andreidodu.europealibrary.resource.impl;

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
    public ResponseEntity<StripeProductDTO> get(Long fileMetaInfoId) throws IOException, StripeException {
        return ResponseEntity.ok(this.stripeProductAssemblerService.getProductAssembly(fileMetaInfoId));
    }

    @Override
    public ResponseEntity<StripeProductDTO> create(StripeProductDTO stripeProductDTO) throws IOException, StripeException {
        return ResponseEntity.ok(this.stripeProductAssemblerService.createNewStripeProductAssembly(stripeProductDTO));
    }

    @Override
    public ResponseEntity<StripeProductDTO> update(StripeProductDTO stripeProductDTO) throws IOException, StripeException {
        return ResponseEntity.ok(this.stripeProductAssemblerService.updateStripeProductAssembly(stripeProductDTO));
    }
}
