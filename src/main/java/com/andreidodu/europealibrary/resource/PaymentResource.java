package com.andreidodu.europealibrary.resource;


import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionRequestDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionResponseDTO;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/payment")
@Tag(name = "Payment", description = "Allows one-time purchase or subscriptions")
public interface PaymentResource {

    @PostMapping("/initCheckoutSession")
    @Operation(summary = "Init the checkout sessions", description = "")
    ResponseEntity<StripeCheckoutSessionResponseDTO> initCheckoutSession(@Parameter(description = "Model containing the product id", example = "{productId: Long}") @Valid @RequestBody StripeCheckoutSessionRequestDTO stripeCheckoutSessionRequestDTO, Authentication authentication) throws StripeException;

    @GetMapping("/checkPurchaseSessionStatus/{purchaseSessionId}")
    @Operation(summary = "", description = "")
    ResponseEntity<OperationStatusDTO> checkPurchaseSessionStatus(@Parameter(description = "", example = "") @PathVariable Long purchaseSessionId, Authentication authentication);

    @PutMapping("/cancel/ongoingPurchaseSessionId/{ongoingPurchaseSessionId}")
    @Operation(summary = "", description = "")
    ResponseEntity<OperationStatusDTO> cancelPurchaseSession(@Parameter(description = "", example = "") @PathVariable Long ongoingPurchaseSessionId, Authentication authentication);


}
