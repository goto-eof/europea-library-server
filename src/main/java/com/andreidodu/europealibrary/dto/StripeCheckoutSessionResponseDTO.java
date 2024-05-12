package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeCheckoutSessionResponseDTO {
    private Long sessionId;
    private String stripePublicKey;
}
