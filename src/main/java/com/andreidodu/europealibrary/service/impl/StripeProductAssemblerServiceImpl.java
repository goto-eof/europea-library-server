package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeProductDTO;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.mapper.stripe.StripePriceMapper;
import com.andreidodu.europealibrary.mapper.stripe.StripeProductMapper;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.stripe.StripePrice;
import com.andreidodu.europealibrary.model.stripe.StripeProduct;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.StripePriceRepository;
import com.andreidodu.europealibrary.repository.StripeProductRepository;
import com.andreidodu.europealibrary.service.*;
import com.andreidodu.europealibrary.util.ValidationUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripeProductAssemblerServiceImpl implements StripeProductAssemblerService {
    private final StripeRemoteProductService stripeRemoteProductService;
    private final StripeRemotePriceService stripeRemotePriceService;

    private final StripeProductRepository stripeProductRepository;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final StripePriceRepository stripePriceRepository;

    private final StripeProductMapper stripeProductMapper;
    private final StripePriceMapper stripePriceMapper;

    @Override
    public StripePriceDTO getProductAssembly(Long fileMetaInfoId) {
        return null;
    }

    @Override
    public StripePriceDTO createNewStripePriceAssembly(StripePriceDTO stripePriceDTO) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO, "dto could not be null");

        StripeProduct stripeProduct = createStripeProduct(stripePriceDTO);
        StripePrice stripePrice = createStripePrice(stripePriceDTO, stripeProduct);
        return this.stripePriceMapper.toDTO(stripePrice);
    }

    private StripeProduct createStripeProduct(StripePriceDTO stripePriceDTO) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO.getStripeProduct(), "StripeProduct could not be null");
        StripeProductDTO stripeProductDTO = stripePriceDTO.getStripeProduct();
        ValidationUtil.assertNotNull(stripeProductDTO.getFileMetaInfoId(), "FileMetaInfoId could not be null");
        ValidationUtil.assertNotNull(stripeProductDTO.getName(), "StripeProduct name could not be null");
        ValidationUtil.assertNotNull(stripeProductDTO.getDescription(), "StripeProduct description could not be null");

        if (stripeProductRepository.existsByFileMetaInfo_id(stripeProductDTO.getFileMetaInfoId())) {
            throw new ValidationException("Product already exists");
        }

        FileMetaInfo fileMetaInfo = this.fileMetaInfoRepository.findById(stripeProductDTO.getFileMetaInfoId())
                .orElseThrow(() -> new ValidationException("FileMetaInfo does not exists"));

        StripeProduct stripeProduct = this.stripeProductMapper.toModel(stripeProductDTO);
        stripeProduct.setFileMetaInfo(fileMetaInfo);

        Product product = this.stripeRemoteProductService.createNewStripeProduct(stripeProductDTO.getName(), stripeProductDTO.getDescription());

        stripeProduct.setStripeProductId(product.getId());
        return this.stripeProductRepository.save(stripeProduct);
    }


    private StripePrice createStripePrice(StripePriceDTO stripePriceDTO, StripeProduct stripeProduct) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO.getAmount(), "StripePrice amount could not be null");
        ValidationUtil.assertNotNull(stripeProduct.getStripeProductId(), "StripeProductId could not be null");
        ValidationUtil.assertNotNull(stripeProduct, "StripeProduct could not be null");

        StripePrice stripePrice = this.stripePriceMapper.toModel(stripePriceDTO);
        stripePrice.setStripeProduct(stripeProduct);
        Price price = this.stripeRemotePriceService.createNewStripePrice(stripeProduct.getStripeProductId(), stripePriceDTO.getAmount().longValue(), null);
        stripePrice.setStripePriceId(price.getId());
        return this.stripePriceRepository.save(stripePrice);
    }

    @Override
    public StripePriceDTO updateStripeProductAssembly(StripePriceDTO stripePriceDTO) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO, "dto could not be null");

        StripeProduct stripeProduct = this.updateStripeProduct(stripePriceDTO);

        deleteStripePrice(stripePriceDTO);

        StripePrice stripePrice = createStripePrice(stripePriceDTO, stripeProduct);
        return this.stripePriceMapper.toDTO(stripePrice);
    }

    private void deleteStripePrice(StripePriceDTO stripePriceDTO) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO.getStripeProduct(), "StripeProduct could not be null");
        ValidationUtil.assertNotNull(stripePriceDTO.getStripeProduct().getFileMetaInfoId(), "FileMetaInfoId could not be null");

        Optional<StripePrice> stripePriceOptional = this.stripePriceRepository.findByStripeProduct_FileMetaInfo_id(stripePriceDTO.getStripeProduct().getFileMetaInfoId());
        if (stripePriceOptional.isPresent()) {
            StripePrice stripePrice = stripePriceOptional.get();
            this.stripeRemotePriceService.deactivate(stripePrice.getStripePriceId());
            this.stripePriceRepository.delete(stripePrice);
        }
    }

    private StripeProduct updateStripeProduct(StripePriceDTO stripePriceDTO) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO.getStripeProduct(), "StripeProduct could not be null");
        StripeProductDTO stripeProductDTO = stripePriceDTO.getStripeProduct();
        ValidationUtil.assertNotNull(stripeProductDTO.getFileMetaInfoId(), "FileMetaInfoId could not be null");
        ValidationUtil.assertNotNull(stripeProductDTO.getName(), "StripeProduct name could not be null");
        ValidationUtil.assertNotNull(stripeProductDTO.getDescription(), "StripeProduct description could not be null");


        FileMetaInfo fileMetaInfo = this.fileMetaInfoRepository.findById(stripeProductDTO.getFileMetaInfoId())
                .orElseThrow(() -> new ValidationException("FileMetaInfo does not exists"));

        StripeProduct stripeProduct = this.stripeProductRepository.findByFileMetaInfo_id(stripeProductDTO.getFileMetaInfoId())
                .orElseThrow(() -> new ValidationException("StripeProduct does not exists"));

        this.stripeProductMapper.map(stripeProduct, stripeProductDTO);

        stripeProduct.setFileMetaInfo(fileMetaInfo);

        this.stripeRemoteProductService.updateStripeProduct(stripeProduct.getStripeProductId(), stripeProductDTO.getName(), stripeProductDTO.getDescription());

        return this.stripeProductRepository.save(stripeProduct);
    }

}
