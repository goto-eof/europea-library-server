package com.andreidodu.europealibrary.mapper.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripeCustomerAddressDTO;
import com.andreidodu.europealibrary.model.stripe.StripeCustomerAddress;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {})
public abstract class StripeCustomerAddressMapper {

    @Mapping(ignore = true, target = "stripeCustomer")
    public abstract StripeCustomerAddress toModel(StripeCustomerAddressDTO dto);

    public abstract StripeCustomerAddressDTO toDTO(StripeCustomerAddress model);

    @Mapping(ignore = true, target = "stripeCustomer")
    public abstract void map(@MappingTarget StripeCustomerAddress model, StripeCustomerAddressDTO dto);
}
