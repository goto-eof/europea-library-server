package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.CursorCommonRequestDTO;
import com.andreidodu.europealibrary.dto.GenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCustomerProductsOwnedDTO;
import com.andreidodu.europealibrary.resource.StripeCustomerProductsOwnedResource;
import com.andreidodu.europealibrary.service.StripeCustomerProductsOwnedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StripeCustomerProductsOwnedResourceImpl implements StripeCustomerProductsOwnedResource {
    private final StripeCustomerProductsOwnedService stripeCustomerProductsOwnedService;

    @Override
    public ResponseEntity<GenericCursoredResponseDTO<String, StripeCustomerProductsOwnedDTO>> retrieveCursored(Authentication authentication, CursorCommonRequestDTO commonRequestDTO) {
        return ResponseEntity.ok(this.stripeCustomerProductsOwnedService.retrieveCursored(authentication.getName(), commonRequestDTO));
    }

}
