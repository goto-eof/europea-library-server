package com.andreidodu.europealibrary.dto.stripe;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import com.stripe.param.PriceCreateParams;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StripePricingPlanDTO extends CommonDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal amount;
    private String currency;
    private PriceCreateParams.Recurring.Interval interval;
    private String stripePricingPlanId;

}
