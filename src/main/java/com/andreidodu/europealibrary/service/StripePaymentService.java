package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.StripeCheckoutSessionRequestDTO;
import com.andreidodu.europealibrary.dto.StripeCheckoutSessionResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public interface StripePaymentService {
    StripeCheckoutSessionResponseDTO initCheckoutSession(StripeCheckoutSessionRequestDTO stripeCheckoutSessionRequestDTO);

    HttpStatus checkoutSessionCompleted(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;

    OperationStatusDTO checkPurchaseSessionStatus(Long purchaseSessionId);

    HttpStatus checkoutSubscriptionSessionCompleted(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
