package com.andreidodu.europealibrary.model.stripe;

import com.andreidodu.europealibrary.enums.StripePurchaseSessionStatus;
import com.andreidodu.europealibrary.model.common.ModelCommon;
import com.andreidodu.europealibrary.model.security.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "el_stripe_purchase_session")
@EntityListeners(AuditingEntityListener.class)
public class StripePurchaseSession extends ModelCommon {

    @Id
    @GeneratedValue(generator = "el_stripe_purchase_session_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_stripe_purchase_session_seq", sequenceName = "el_stripe_purchase_session_seq", allocationSize = 50)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StripePurchaseSessionStatus stripePurchaseSessionStatus;

    @Column(name = "stripe_customer_id")
    private StripeCustomer stripeCustomer;

    @ManyToOne
    @JoinColumn(name = "stripe_product_id", nullable = true, unique = false)
    private StripeProduct stripeProduct;

    @ManyToOne
    @JoinColumn(name = "stripe_pricing_plan_id", nullable = true, unique = false)
    private StripePricingPlan stripePricingPlan;


}
