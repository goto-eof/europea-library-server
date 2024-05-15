package com.andreidodu.europealibrary.dto.stripe;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StripePriceDTO extends CommonDTO {
    private Long id;
    private String currency;
    private BigDecimal amount;
    private StripeProductDTO stripeProduct;
    private Boolean archived;
}
