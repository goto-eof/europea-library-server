package com.andreidodu.europealibrary.resource;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping("/api/v1/stripe/webhook")
@Tag(name = "Payment", description = "Allows one-time purchase or subscriptions")
public interface StripeWebhookResource {

    @PostMapping("/checkoutSessionCompleted")
    @Operation(summary = "Checkout session completed", description = "")
    ResponseEntity<String> checkoutSessionCompleted(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;

}
