package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionRequestDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionResponseDTO;
import com.andreidodu.europealibrary.resource.PaymentResource;
import com.andreidodu.europealibrary.service.StripePaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentResourceImpl implements PaymentResource {
    private final StripePaymentService stripePaymentService;

    @Override
    public ResponseEntity<StripeCheckoutSessionResponseDTO> initCheckoutSession(StripeCheckoutSessionRequestDTO stripeCheckoutSessionRequestDTO, Authentication authentication) throws StripeException {
        return ResponseEntity.ok(this.stripePaymentService.initCheckoutSession(stripeCheckoutSessionRequestDTO, authentication.getName()));
    }

    @Override
    public ResponseEntity<OperationStatusDTO> checkPurchaseSessionStatus(Long purchaseSessionId, Authentication authentication) {
        return ResponseEntity.ok(this.stripePaymentService.checkPurchaseSessionStatus(purchaseSessionId));

    }
}
