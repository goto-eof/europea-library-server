package com.andreidodu.europealibrary.mapper.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripeCustomerProductsOwnedDTO;
import com.andreidodu.europealibrary.mapper.FileMetaInfoMapper;
import com.andreidodu.europealibrary.model.stripe.StripeCustomerProductsOwned;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {StripeProductMapper.class, StripePriceMapper.class, FileMetaInfoMapper.class})
public abstract class StripeCustomerProductOwnedMapper {

    @Mapping(source = "stripeProduct.fileMetaInfo", target = "fileMetaInfo")
    public abstract StripeCustomerProductsOwnedDTO toDTO(StripeCustomerProductsOwned model);

}
