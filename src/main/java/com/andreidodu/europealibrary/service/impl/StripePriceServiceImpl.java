package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.mapper.stripe.StripePriceMapper;
import com.andreidodu.europealibrary.model.stripe.StripePrice;
import com.andreidodu.europealibrary.model.stripe.StripeProduct;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.StripePriceRepository;
import com.andreidodu.europealibrary.repository.StripeProductRepository;
import com.andreidodu.europealibrary.service.StripePriceService;
import com.andreidodu.europealibrary.service.StripeRemotePriceService;
import com.andreidodu.europealibrary.util.ValidationUtil;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripePriceServiceImpl implements StripePriceService {
    private final StripeRemotePriceService stripeRemotePriceService;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final StripePriceRepository stripePriceRepository;
    private final StripeProductRepository stripeProductRepository;
    private final StripePriceMapper stripePriceMapper;

    @Override
    public StripePriceDTO getStripePrice(Long fileMetaInfoId) {
        ValidationUtil.assertNotNull(fileMetaInfoId, "FileMetaInfoId could not be null");
        StripePrice stripePrice = this.stripePriceRepository.findByStripeProduct_FileMetaInfo_id(fileMetaInfoId)
                .orElseThrow(() -> new ValidationException("StripePrice does not exists"));
        return this.stripePriceMapper.toDTO(stripePrice);
    }

    @Override
    public StripePriceDTO createStripePrice(StripePriceDTO stripePriceDTO) throws StripeException {
        StripeProduct stripeProduct = this.stripeProductRepository.findById(stripePriceDTO.getStripeProductId())
                .orElseThrow(() -> new ValidationException("Product does not exists"));
        StripePrice stripePrice = this.stripePriceMapper.toModel(stripePriceDTO);
        stripePrice.setStripeProduct(stripeProduct);
        String stripePriceId = this.stripeRemotePriceService.createNewStripePrice(stripeProduct.getStripeProductId(), stripePriceDTO.getAmount().longValue(), null);
        stripePrice.setStripePriceId(stripePriceId);
        stripePrice = this.stripePriceRepository.save(stripePrice);
        return this.stripePriceMapper.toDTO(stripePrice);
    }

    @Override
    public StripePriceDTO updateStripePrice(StripePriceDTO stripePriceDTO) throws StripeException {
        StripePrice stripePrice = this.stripePriceRepository.findById(stripePriceDTO.getId())
                .orElseThrow(() -> new ValidationException("StripePrice does not exists"));

        BigDecimal oldAmount = stripePrice.getAmount();
        BigDecimal newAmount = stripePriceDTO.getAmount();

        this.stripePriceMapper.map(stripePrice, stripePriceDTO);

        if (oldAmount.compareTo(newAmount) != 0) {
            String stripePriceId = this.stripeRemotePriceService.createNewStripePrice(stripePrice.getStripeProduct().getStripeProductId(), stripePriceDTO.getAmount().longValue(), null);
            stripePrice.setStripePriceId(stripePriceId);
        }

        stripePrice = this.stripePriceRepository.save(stripePrice);

        return this.stripePriceMapper.toDTO(stripePrice);
    }

    @Override
    public OperationStatusDTO deleteStripePrice(Long fileMetaInfoId) {
        StripePrice stripePrice = this.stripePriceRepository.findByStripeProduct_FileMetaInfo_id(fileMetaInfoId)
                .orElseThrow(() -> new ValidationException("StripePrice does not exists"));
        this.stripePriceRepository.delete(stripePrice);
        return new OperationStatusDTO(true, "Entity deleted successfully");
    }


}
