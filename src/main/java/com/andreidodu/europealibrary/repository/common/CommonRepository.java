package com.andreidodu.europealibrary.repository.common;

import com.andreidodu.europealibrary.dto.PaginatedExplorerOptions;
import com.andreidodu.europealibrary.model.QFileMetaInfo;
import com.andreidodu.europealibrary.model.stripe.QStripeCustomerProductsOwned;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;

public abstract class CommonRepository {
    public static final QStripeCustomerProductsOwned STRIPE_CUSTOMER_PRODUCTS_OWNED = QStripeCustomerProductsOwned.stripeCustomerProductsOwned;


    protected void applyCommonFilter(QFileMetaInfo fileMetaInfo, PaginatedExplorerOptions paginatedExplorerOptions, BooleanBuilder booleanBuilder) {
        if (paginatedExplorerOptions.getUsername() == null) {
            booleanBuilder.and(fileMetaInfo.hidden.eq(false));
            return;
        }
        if (!paginatedExplorerOptions.isAdministratorFlag()) {
            booleanBuilder.and(
                    fileMetaInfo.isNull()
                            .or(isProductOwner(fileMetaInfo, paginatedExplorerOptions, booleanBuilder))
                            .or(fileMetaInfo.hidden.eq(false))
            );
        } // else is admin and allow to view hidden/unhidden e-books
    }

    protected Predicate isProductOwner(QFileMetaInfo fileMetaInfo, PaginatedExplorerOptions paginatedExplorerOptions, BooleanBuilder booleanBuilder) {

        if (paginatedExplorerOptions.getUsername() == null) {
            return Expressions.asBoolean(false).isTrue();
        }

        return JPAExpressions
                .select(fileMetaInfo)
                .from(fileMetaInfo)
                .where(fileMetaInfo.id.in(JPAExpressions.selectDistinct(STRIPE_CUSTOMER_PRODUCTS_OWNED.stripeProduct.fileMetaInfo.id)
                        .from(STRIPE_CUSTOMER_PRODUCTS_OWNED)
                        .where(STRIPE_CUSTOMER_PRODUCTS_OWNED.stripeCustomer.user.username.eq(paginatedExplorerOptions.getUsername()))
                ))
                .exists();
    }
}
