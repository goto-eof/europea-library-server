package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.StripeCustomerDTO;
import com.andreidodu.europealibrary.model.stripe.StripeCustomer;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {StripeCustomerPricingPlanOwnedMapper.class, StripeCustomerProductOwnedMapper.class})
public abstract class StripeCustomerMapper {

    public abstract StripeCustomer toModel(StripeCustomerDTO dto);

    public abstract StripeCustomerDTO toDTO(StripeCustomer model);

}
