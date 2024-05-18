package com.andreidodu.europealibrary.resource;


import com.andreidodu.europealibrary.dto.CursorCommonRequestDTO;
import com.andreidodu.europealibrary.dto.GenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCustomerProductsOwnedDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/stripe/purchases")
@Tag(name = "Stripe Purchase Manager", description = "")
public interface StripeCustomerProductsOwnedResource {

    @PostMapping(path = "/cursored")
    ResponseEntity<GenericCursoredResponseDTO<String, StripeCustomerProductsOwnedDTO>> retrieveCursored(Authentication authentication, @Valid @RequestBody CursorCommonRequestDTO commonRequestDTO);

}
