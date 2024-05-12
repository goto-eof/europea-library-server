package com.andreidodu.europealibrary.resource;


import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionRequestDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionResponseDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCustomerDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeUserRegistrationRequestDTO;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/stripe")
@Tag(name = "Stripe Payment", description = "Allows one-time purchase or subscriptions")
public interface StripeResource {

    @PostMapping("/isCustomer")
    @Operation(summary = "", description = "")
    ResponseEntity<OperationStatusDTO> isStripeCustomer(Authentication authentication);

    @PostMapping("/createCustomer")
    @Operation(summary = "", description = "")
    ResponseEntity<StripeCustomerDTO> createStripeCustomer(@RequestBody @Valid StripeUserRegistrationRequestDTO stripeUserRegistrationRequestDTO);

    @PostMapping("/initCheckoutSession")
    @Operation(summary = "", description = "")
    ResponseEntity<StripeCheckoutSessionResponseDTO> initCheckoutSession(@RequestBody @Valid StripeCheckoutSessionRequestDTO stripeCheckoutSessionRequestDTO, Authentication authentication) throws StripeException;

    @GetMapping("/isCheckoutPurchaseSessionCompleted/{purchaseSessionId}")
    @Operation(summary = "", description = "")
    ResponseEntity<OperationStatusDTO> isCheckoutPurchaseSessionCompleted(@PathVariable Long purchaseSessionId);

}
