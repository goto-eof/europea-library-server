package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.enums.StripeCurrency;
import com.andreidodu.europealibrary.service.StripeRemotePriceService;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.param.PriceCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripeRemotePriceServiceImpl implements StripeRemotePriceService {

    @Override
    public String createNewStripePrice(String stripeProductId, Long amount, PriceCreateParams.Recurring.Interval interval) throws StripeException {
        Price price = null;
        if (interval != null) {
            price = this.createSubscriptionPrice(stripeProductId, StripeCurrency.EUR, amount, interval);
        } else {
            price = this.createOneShotPrice(stripeProductId, StripeCurrency.EUR, amount);
        }
        return price.getId();
    }

    private Price createOneShotPrice(String stripeProductId, StripeCurrency currency, Long amount) throws StripeException {
        PriceCreateParams.Builder priceCreateParmBuilder = createPriceCommon(stripeProductId, currency, amount);
        return Price.create(priceCreateParmBuilder.build());
    }

    private Price createSubscriptionPrice(String stripeProductId, StripeCurrency currency, Long amount, PriceCreateParams.Recurring.Interval interval) throws StripeException {
        PriceCreateParams.Builder priceCreateParmBuilder = createPriceCommon(stripeProductId, currency, amount);
        priceCreateParmBuilder.setRecurring(PriceCreateParams.Recurring.builder().setInterval(interval).build());
        return Price.create(priceCreateParmBuilder.build());
    }

    private static PriceCreateParams.Builder createPriceCommon(String stripeProductId, StripeCurrency currency, Long amount) {
        return PriceCreateParams.builder()
                .setCurrency(currency.name())
                .setUnitAmount(amount)
                .setProduct(stripeProductId);
    }

}
