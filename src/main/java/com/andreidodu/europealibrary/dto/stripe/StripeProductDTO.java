package com.andreidodu.europealibrary.dto.stripe;

import com.andreidodu.europealibrary.dto.common.CommonDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeProductDTO extends CommonDTO {
    private Long id;
    private String name;
    private String description;
    private Long fileMetaInfoId;
}
