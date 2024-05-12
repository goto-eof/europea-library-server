package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeCustomerProductsOwnedDTO extends CommonDTO {
    private Long id;
    private StripeProductDTO stripeProduct;
}
