package com.andreidodu.europealibrary.resource;


import com.andreidodu.europealibrary.dto.stripe.StripeProductDTO;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/api/v1/stripe/product")
@Tag(name = "Stripe Product", description = "Allows add/remove/update stripe products")
public interface StripeProductAssemblerResource {

    @GetMapping("/fileMetaInfoId/{fileMetaInfoId}")
    @Operation(summary = "Retrieve Stripe Product", description = "Retrieves a Stripe Product associated to a FileMetaInfo")
    ResponseEntity<StripeProductDTO> get(@PathVariable @NotNull Long fileMetaInfoId) throws IOException, StripeException;

    @PostMapping
    @Operation(summary = "Create Stripe Product", description = "Creates a Stripe Product associated to a FileMetaInfo")
    ResponseEntity<StripeProductDTO> create(@RequestBody @NotNull @Valid StripeProductDTO stripeProductDTO) throws IOException, StripeException;

    @PutMapping
    @Operation(summary = "Update Stripe Product", description = "Updates a Stripe Product associated to a FileMetaInfo")
    ResponseEntity<StripeProductDTO> update(@RequestBody @NotNull @Valid StripeProductDTO stripeProductDTO) throws IOException, StripeException;

}
