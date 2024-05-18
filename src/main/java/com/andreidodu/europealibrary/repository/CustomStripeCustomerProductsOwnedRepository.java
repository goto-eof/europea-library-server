package com.andreidodu.europealibrary.repository;


import com.andreidodu.europealibrary.dto.CursorCommonRequestDTO;
import com.andreidodu.europealibrary.model.stripe.StripeCustomerProductsOwned;

import java.util.List;

public interface CustomStripeCustomerProductsOwnedRepository {
    List<StripeCustomerProductsOwned> retrieveCursoredByUsername(String username, CursorCommonRequestDTO cursorRequestDTO);
}
