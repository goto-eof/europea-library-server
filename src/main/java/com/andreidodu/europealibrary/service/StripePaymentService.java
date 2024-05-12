package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionRequestDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionResponseDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCustomerDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeUserRegistrationRequestDTO;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public interface StripePaymentService {

    OperationStatusDTO isStripeCustomer(String email);

    StripeCustomerDTO createStripeCustomer(StripeUserRegistrationRequestDTO stripeUserRegistrationRequestDTO);

    StripeCheckoutSessionResponseDTO initCheckoutSession(StripeCheckoutSessionRequestDTO stripeCheckoutSessionRequestDTO, String username) throws StripeException;

    HttpStatus checkoutSessionCompleted(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;

    OperationStatusDTO isCheckoutPurchaseSessionCompleted(Long purchaseSessionId);

}
