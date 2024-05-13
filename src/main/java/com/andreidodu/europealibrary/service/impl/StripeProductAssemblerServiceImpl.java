package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeProductDTO;
import com.andreidodu.europealibrary.service.StripePriceService;
import com.andreidodu.europealibrary.service.StripeProductAssemblerService;
import com.andreidodu.europealibrary.service.StripeProductService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripeProductAssemblerServiceImpl implements StripeProductAssemblerService {
    private final StripeProductService stripeProductService;
    private final StripePriceService stripePriceService;

    @Override
    public StripeProductDTO getProductAssembly(Long fileMetaInfoId) {
        StripeProductDTO stripeProductDTO = this.stripeProductService.getByFileMetaInfoId(fileMetaInfoId);
        stripeProductDTO.setStripePrice(this.stripePriceService.getStripePrice(fileMetaInfoId));
        return stripeProductDTO;
    }

    @Override
    public StripeProductDTO createNewStripeProductAssembly(StripeProductDTO stripeProductDTO) throws StripeException {
        StripeProductDTO savedStripeProductDTO = this.stripeProductService.create(stripeProductDTO);
        StripePriceDTO stripePriceDTO = stripeProductDTO.getStripePrice();
        stripePriceDTO.setStripeProductId(savedStripeProductDTO.getId());
        StripePriceDTO savedStripePriceDTO = this.stripePriceService.createStripePrice(stripePriceDTO);
        savedStripeProductDTO.setStripePrice(savedStripePriceDTO);
        return savedStripeProductDTO;
    }

    @Override
    public StripeProductDTO updateStripeProductAssembly(StripeProductDTO stripeProductDTO) throws StripeException {
        StripeProductDTO savedStripeProductDTO = this.stripeProductService.update(stripeProductDTO);
        StripePriceDTO stripePriceDTO = stripeProductDTO.getStripePrice();
        stripePriceDTO.setStripeProductId(savedStripeProductDTO.getId());
        StripePriceDTO savedStripePriceDTO = this.stripePriceService.updateStripePrice(stripePriceDTO);
        savedStripeProductDTO.setStripePrice(savedStripePriceDTO);
        return savedStripeProductDTO;
    }

}
