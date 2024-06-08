package com.andreidodu.europealibrary.model.stripe;

import com.andreidodu.europealibrary.enums.StripeCustomerProductsOwnedStatus;
import com.andreidodu.europealibrary.model.common.Identifiable;
import com.andreidodu.europealibrary.model.common.ModelCommon;
import com.andreidodu.europealibrary.model.security.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "el_stripe_customer_product_owned")
@EntityListeners(AuditingEntityListener.class)
public class StripeCustomerProductsOwned extends ModelCommon implements Identifiable {

    @Id
    @GeneratedValue(generator = "el_stripe_customer_product_owned_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_stripe_customer_product_owned_seq", sequenceName = "el_stripe_customer_product_owned_seq", allocationSize = 50)

    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stripe_customer_id", nullable = false)
    private StripeCustomer stripeCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stripe_product_id", nullable = false)
    private StripeProduct stripeProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stripe_price_id", nullable = false)
    private StripePrice stripePrice;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stripe_purchase_session_id", unique = true, nullable = false)
    private StripePurchaseSession stripePurchaseSession;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StripeCustomerProductsOwnedStatus status;

}
