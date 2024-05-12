package com.andreidodu.europealibrary.resource;


import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionRequestDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/payment")
@Tag(name = "Payment", description = "Allows one-time purchase or subscriptions")
public interface PaymentResource {

    @PostMapping("/initCheckoutSession")
    @Operation(summary = "Init the checkout sessions", description = "")
    ResponseEntity<StripeCheckoutSessionResponseDTO> initCheckoutSession(@Parameter(description = "Model containing the product id", example = "{productId: Long}") @Valid @RequestBody StripeCheckoutSessionRequestDTO stripeCheckoutSessionRequestDTO, Authentication authentication);

    @PostMapping("/checkPurchaseSessionStatus/{purchaseSessionId}")
    @Operation(summary = "", description = "")
    ResponseEntity<OperationStatusDTO> checkPurchaseSessionStatus(@Parameter(description = "", example = "") @PathVariable Long purchaseSessionId, Authentication authentication);


}
