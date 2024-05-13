package com.andreidodu.europealibrary.mapper.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripePricingPlanDTO;
import com.andreidodu.europealibrary.model.stripe.StripePricingPlan;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {})
public abstract class StripePricingPlanMapper {

    @Mapping(ignore = true, target = "stripePurchaseSessionList")
    public abstract StripePricingPlan toModel(StripePricingPlanDTO dto);

    public abstract StripePricingPlanDTO toDTO(StripePricingPlan model);

    @Mapping(ignore = true, target = "stripePurchaseSessionList")
    public abstract void map(@MappingTarget StripePricingPlan target, StripePricingPlanDTO source);


}
