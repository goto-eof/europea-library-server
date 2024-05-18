package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CursorCommonRequestDTO;
import com.andreidodu.europealibrary.enums.StripeCustomerProductsOwnedStatus;
import com.andreidodu.europealibrary.model.stripe.QStripeCustomerProductsOwned;
import com.andreidodu.europealibrary.model.stripe.StripeCustomerProductsOwned;
import com.andreidodu.europealibrary.repository.CustomStripeCustomerProductsOwnedRepository;
import com.andreidodu.europealibrary.util.LimitUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomStripeCustomerProductsOwnedRepositoryImpl implements CustomStripeCustomerProductsOwnedRepository {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<StripeCustomerProductsOwned> retrieveCursoredByUsername(String username, CursorCommonRequestDTO cursorRequestDTO) {
        QStripeCustomerProductsOwned stripeCustomerProductsOwned = QStripeCustomerProductsOwned.stripeCustomerProductsOwned;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        int numberOfResults = LimitUtil.calculateLimit(cursorRequestDTO.getLimit(), ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);
        Optional.ofNullable(cursorRequestDTO.getNextCursor())
                .ifPresent((cursorIdValue) -> booleanBuilder.and(stripeCustomerProductsOwned.id.goe(cursorIdValue)));

        booleanBuilder.and(stripeCustomerProductsOwned.stripeCustomer.user.username.eq(username));
        booleanBuilder.and(stripeCustomerProductsOwned.status.eq(StripeCustomerProductsOwnedStatus.PURCHASED));

        OrderSpecifier<?>[] customOrder = new OrderSpecifier[]{
                stripeCustomerProductsOwned.id.desc()
        };

        return new JPAQuery<StripeCustomerProductsOwned>(entityManager)
                .select(stripeCustomerProductsOwned)
                .from(stripeCustomerProductsOwned)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .orderBy(customOrder)
                .fetch();
    }
}
