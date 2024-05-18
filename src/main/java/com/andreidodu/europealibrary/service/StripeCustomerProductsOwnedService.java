package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.CursorCommonRequestDTO;
import com.andreidodu.europealibrary.dto.GenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCustomerProductsOwnedDTO;

public interface StripeCustomerProductsOwnedService {
    GenericCursoredResponseDTO<String, StripeCustomerProductsOwnedDTO> retrieveCursored(String username, CursorCommonRequestDTO commonRequestDTO);
}
