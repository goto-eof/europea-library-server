package com.andreidodu.europealibrary.mapper;

import com.andreidodu.europealibrary.dto.common.FileMetaInfoDTO;
import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeProductDTO;
import com.andreidodu.europealibrary.mapper.stripe.StripePriceMapper;
import com.andreidodu.europealibrary.mapper.stripe.StripeProductMapper;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.stripe.StripePrice;
import com.andreidodu.europealibrary.model.stripe.StripeProduct;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {
        TagMapper.class,
        BookInfoMapper.class,
        StripePriceMapper.class,
        StripeProductMapper.class
})
public abstract class FileMetaInfoMapper {

    @Autowired
    private StripeProductMapper stripeProductMapper;
    @Autowired
    private StripePriceMapper stripePriceMapper;

    @Mapping(target = "stripePrice", source = "stripeProduct.currentStripePrice")
    @Mapping(target = "stripePrice.stripeProduct", source = "stripeProduct")
    @Mapping(ignore = true, target = "downloadable")
    public abstract FileMetaInfoDTO toDTO(FileMetaInfo model);

    @Named(value = "toDTOWithRevertedPriceProduct")
    public FileMetaInfoDTO toDTOWithRevertedPriceProduct(FileMetaInfo model) {
        FileMetaInfoDTO result = this.toDTO(model);
        StripeProduct stripeProduct = model.getStripeProduct();
        if (stripeProduct != null) {
            StripePrice stripePrice = stripeProduct.getCurrentStripePrice();
            if (stripePrice != null) {
                StripePriceDTO stripePriceDTO = this.stripePriceMapper.toDTO(stripePrice);
                StripeProductDTO stripeProductDTO = this.stripeProductMapper.toDTO(stripeProduct);
                stripePriceDTO.setStripeProduct(stripeProductDTO);
                result.setStripePrice(stripePriceDTO);
            }
        }
        return result;
    }

//    @Mapping(ignore = true, target = "stripePrice")
//    public abstract void map(@MappingTarget FileMetaInfoDTO target, FileMetaInfo source);

    @Mapping(ignore = true, target = "fileSystemItemList")
    @Mapping(ignore = true, target = "bookInfo")
    @Mapping(ignore = true, target = "tagList")
    @Mapping(ignore = true, target = "stripeProduct")
    @Mapping(ignore = true, target = "applicationSettings")
    public abstract void map(@MappingTarget FileMetaInfo target, FileMetaInfoDTO source);

}
