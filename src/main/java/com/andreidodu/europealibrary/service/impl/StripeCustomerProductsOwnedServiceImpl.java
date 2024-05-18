package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CursorCommonRequestDTO;
import com.andreidodu.europealibrary.dto.GenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCustomerProductsOwnedDTO;
import com.andreidodu.europealibrary.mapper.stripe.StripeCustomerProductOwnedMapper;
import com.andreidodu.europealibrary.model.stripe.StripeCustomerProductsOwned;
import com.andreidodu.europealibrary.repository.StripeCustomerProductsOwnedRepository;
import com.andreidodu.europealibrary.service.StripeCustomerProductsOwnedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripeCustomerProductsOwnedServiceImpl extends CursoredServiceCommon implements StripeCustomerProductsOwnedService {
    private final StripeCustomerProductsOwnedRepository stripeCustomerProductsOwnedRepository;
    private final StripeCustomerProductOwnedMapper stripeCustomerProductOwnedMapper;

    @Override
    public GenericCursoredResponseDTO<String, StripeCustomerProductsOwnedDTO> retrieveCursored(String username, CursorCommonRequestDTO commonRequestDTO) {
        List<StripeCustomerProductsOwned> children = this.stripeCustomerProductsOwnedRepository.retrieveCursoredByUsername(username, commonRequestDTO);
        GenericCursoredResponseDTO<String, StripeCustomerProductsOwnedDTO> cursoredResult = new GenericCursoredResponseDTO<>();
        List<StripeCustomerProductsOwned> childrenList = limit(children, commonRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        cursoredResult.setChildrenList(childrenList.stream()
                .map(this.stripeCustomerProductOwnedMapper::toDTO)
                .collect(Collectors.toList()));
        super.calculateNextId(children, commonRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE)
                .ifPresent(cursoredResult::setNextCursor);
        cursoredResult.setParent("Stripe Customer Products Owned");
        return cursoredResult;
    }
}
