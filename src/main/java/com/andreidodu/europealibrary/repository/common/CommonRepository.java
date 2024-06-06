package com.andreidodu.europealibrary.repository.common;

import com.andreidodu.europealibrary.dto.PaginatedExplorerOptions;
import com.andreidodu.europealibrary.model.QFileMetaInfo;
import com.andreidodu.europealibrary.model.stripe.QStripeCustomerProductsOwned;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;

public abstract class CommonRepository {
    public static final QStripeCustomerProductsOwned stripeCustomerProductsOwned = QStripeCustomerProductsOwned.stripeCustomerProductsOwned;

    protected void applyCommonFilter(QFileMetaInfo fileMetaInfo, PaginatedExplorerOptions paginatedExplorerOptions, BooleanBuilder booleanBuilder) {
        this.filterHiddenFilesIfNecessary(fileMetaInfo, paginatedExplorerOptions, booleanBuilder);
    }

    protected void filterHiddenFilesIfNecessary(QFileMetaInfo fileMetaInfo, PaginatedExplorerOptions paginatedExplorerOptions, BooleanBuilder booleanBuilder) {
        // user not authenticated, then retrieve only not hidden e-books
        if (paginatedExplorerOptions.getUsername() == null) {
            booleanBuilder.and(
                    fileMetaInfo.isNull()
                            .or(fileMetaInfo.hidden.eq(false))
            );
            return;
        }
        // is administrator, then retrieve all e-books
        if (paginatedExplorerOptions.isAdministratorFlag()) {
            booleanBuilder.and(
                    fileMetaInfo.isNull()
                            .or(fileMetaInfo.hidden.eq(false))
                            .or(fileMetaInfo.hidden.eq(true))
            );
            return;
        }
        // is e-book owner, then allow to retrieve the hidden e-book
        booleanBuilder.and(
                fileMetaInfo.isNull()
                        .or(fileMetaInfo.hidden.eq(false))
                        .or(isProductOwner(fileMetaInfo, paginatedExplorerOptions, booleanBuilder))
        );
    }

    protected Predicate isProductOwner(QFileMetaInfo fileMetaInfo, PaginatedExplorerOptions paginatedExplorerOptions, BooleanBuilder booleanBuilder) {
        return JPAExpressions
                .select(fileMetaInfo)
                .from(fileMetaInfo)
                .where(
                        fileMetaInfo.id.in(
                                JPAExpressions
                                        .selectDistinct(stripeCustomerProductsOwned.stripeProduct.fileMetaInfo.id)
                                        .from(stripeCustomerProductsOwned)
                                        .where(stripeCustomerProductsOwned.stripeCustomer.user.username.eq(paginatedExplorerOptions.getUsername())
                                        )
                        )
                )
                .exists();
    }
}
