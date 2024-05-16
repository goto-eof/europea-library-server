package com.andreidodu.europealibrary.resource;


import com.andreidodu.europealibrary.dto.stripe.StripeCustomerDTO;
import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/api/v1/stripe/customer")
@Tag(name = "Stripe Customer", description = "")
public interface StripeCustomerAssemblerResource {

    @GetMapping
    @Operation(summary = "", description = "")
    ResponseEntity<StripeCustomerDTO> get(Authentication authentication) throws IOException, StripeException;

    @PostMapping
    @Operation(summary = "", description = "")
    ResponseEntity<StripeCustomerDTO> create(Authentication authentication, @RequestBody @Valid StripeCustomerDTO stripeCustomerDTO) throws IOException, StripeException;

    @PutMapping
    @Operation(summary = "", description = "")
    ResponseEntity<StripeCustomerDTO> update(Authentication authentication, @RequestBody @NotNull @Valid StripeCustomerDTO stripeCustomerDTO) throws IOException, StripeException;

}
