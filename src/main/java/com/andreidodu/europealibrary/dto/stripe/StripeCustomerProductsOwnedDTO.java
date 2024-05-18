package com.andreidodu.europealibrary.dto.stripe;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import com.andreidodu.europealibrary.dto.common.FileMetaInfoDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeCustomerProductsOwnedDTO extends CommonDTO {
    private Long id;
    private StripeProductDTO stripeProduct;
    private StripePriceDTO stripePrice;
    private FileMetaInfoDTO fileMetaInfo;
}
