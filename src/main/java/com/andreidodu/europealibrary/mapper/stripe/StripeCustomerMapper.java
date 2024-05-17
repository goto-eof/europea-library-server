package com.andreidodu.europealibrary.mapper.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripeCustomerDTO;
import com.andreidodu.europealibrary.model.stripe.StripeCustomer;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {StripeCustomerProductOwnedMapper.class, StripeCustomerAddressMapper.class})
public abstract class StripeCustomerMapper {

    @Mapping(ignore = true, target = "user")
    @Mapping(ignore = true, target = "stripeCustomerId")
    public abstract StripeCustomer toModel(StripeCustomerDTO dto);

    @Mapping(ignore = true, target = "user")
    @Mapping(ignore = true, target = "currentStripeCustomerAddress")
    @Mapping(ignore = true, target = "stripeCustomerId")
    public abstract StripeCustomer toModelWithoutAddress(StripeCustomerDTO dto);

    public abstract StripeCustomerDTO toDTO(StripeCustomer model);

    @Mapping(ignore = true, target = "user")
    @Mapping(ignore = true, target = "stripeCustomerId")
    public abstract void map(@MappingTarget StripeCustomer stripeCustomer, StripeCustomerDTO stripeCustomerDTO);
}
