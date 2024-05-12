package com.andreidodu.europealibrary.dto.stripe;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeCheckoutSessionRequestDTO {
    private Long fileMetaInfoId;
    private String checkoutBaseUrl;
    private Long quantity;
}
