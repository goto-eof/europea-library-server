package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.stripe.StripePricingPlanDTO;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.mapper.stripe.StripePricingPlanMapper;
import com.andreidodu.europealibrary.model.stripe.StripePricingPlan;
import com.andreidodu.europealibrary.repository.StripePricingPlanRepository;
import com.andreidodu.europealibrary.service.StripePricingPlanService;
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
public class StripePricingPlanServiceImpl implements StripePricingPlanService {
    private final StripePricingPlanRepository stripePricingPlanRepository;
    private final StripeRemotePriceService stripeRemotePriceService;
    private final StripePricingPlanMapper stripePricingPlanMapper;


    @Override
    public StripePricingPlanDTO getStripePricingPlan(Long stripePricingPlanId) {
        ValidationUtil.assertNotNull(stripePricingPlanId, "StripePricingPlanId could not be null");
        StripePricingPlan stripePricingPlan = this.stripePricingPlanRepository.findById(stripePricingPlanId)
                .orElseThrow(() -> new ValidationException("StripePricingPlan does not exists"));
        return this.stripePricingPlanMapper.toDTO(stripePricingPlan);
    }


    @Override
    public StripePricingPlanDTO createStripePricingPlan(StripePricingPlanDTO stripePricingPlanDTO) throws StripeException {
        StripePricingPlan stripePricingPlan = this.stripePricingPlanMapper.toModel(stripePricingPlanDTO);
//        String stripePriceId = this.stripeRemotePriceService.createNewStripePrice(stripePricingPlanDTO. stripePricingPlan.getAmount().longValue(), stripePricingPlanDTO.getInterval());
        String stripePriceId = null;
        stripePricingPlan.setStripePricingPlanId(stripePriceId);
        stripePricingPlan = this.stripePricingPlanRepository.save(stripePricingPlan);
        return this.stripePricingPlanMapper.toDTO(stripePricingPlan);
    }


    @Override
    public StripePricingPlanDTO updateStripePricingPlan(StripePricingPlanDTO stripePricingPlanDTO) throws StripeException {
        StripePricingPlan stripePricingPlan = this.stripePricingPlanRepository.findById(stripePricingPlanDTO.getId())
                .orElseThrow(() -> new ValidationException("StripePricingPlan does not exists"));

        BigDecimal oldAmount = stripePricingPlan.getAmount();
        BigDecimal newAmount = stripePricingPlanDTO.getAmount();

        this.stripePricingPlanMapper.map(stripePricingPlan, stripePricingPlanDTO);
//
//        if (oldAmount.compareTo(newAmount) != 0) {
//            String stripePriceId = this.stripeRemotePriceService.createNewStripePrice(stripePricingPlanDTO.getAmount().longValue(), stripePricingPlanDTO.getInterval());
//            stripePricingPlan.setStripePricingPlanId(stripePriceId);
//        }

        stripePricingPlan = this.stripePricingPlanRepository.save(stripePricingPlan);

        return this.stripePricingPlanMapper.toDTO(stripePricingPlan);
    }

    @Override
    public OperationStatusDTO deleteStripePricingPlan(Long stripePricingPlanId) {
        StripePricingPlan model = this.stripePricingPlanRepository.findById(stripePricingPlanId)
                .orElseThrow(() -> new ValidationException("StripePrice does not exists"));
        this.stripePricingPlanRepository.delete(model);
        return new OperationStatusDTO(true, "Entity deleted successfully");
    }

}
