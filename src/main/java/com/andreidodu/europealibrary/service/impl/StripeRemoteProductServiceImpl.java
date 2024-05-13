package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.service.StripeRemoteProductService;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.param.ProductCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripeRemoteProductServiceImpl implements StripeRemoteProductService {

    @Override
    public String createNewStripeProduct(String name, String description) throws StripeException {
        ProductCreateParams params =
                ProductCreateParams.builder()
                        .setName(name)
                        .setDescription(description)
                        .build();
        return Product.create(params).getId();
    }

}
