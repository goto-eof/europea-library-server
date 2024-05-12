package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.resource.StripeWebhookResource;
import com.andreidodu.europealibrary.service.StripePaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StripeWebhookResourceImpl implements StripeWebhookResource {
    private final StripePaymentService stripePaymentService;

    @Override
    public ResponseEntity<String> checkoutSessionCompleted(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return ResponseEntity
                .status(this.stripePaymentService.checkoutSessionCompleted(httpServletRequest, httpServletResponse))
                .body("");
    }

}
