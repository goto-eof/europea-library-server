package com.andreidodu.europealibrary.mapper.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripeCustomerProductsOwnedDTO;
import com.andreidodu.europealibrary.model.stripe.StripeCustomerProductsOwned;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {StripeProductMapper.class})
public abstract class StripeCustomerProductOwnedMapper {

    @Mapping(ignore = true, target = "stripeCustomer")
    @Mapping(ignore = true, target = "stripePrice")
    public abstract StripeCustomerProductsOwned toModel(StripeCustomerProductsOwnedDTO dto);

    public abstract StripeCustomerProductsOwnedDTO toDTO(StripeCustomerProductsOwned model);

}
