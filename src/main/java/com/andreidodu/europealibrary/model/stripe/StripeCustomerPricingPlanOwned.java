package com.andreidodu.europealibrary.model.stripe;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "el_stripe_customer_pricing_plan_owned")
@EntityListeners(AuditingEntityListener.class)
public class StripeCustomerPricingPlanOwned extends ModelCommon {

    @Id
    @GeneratedValue(generator = "el_stripe_customer_pricing_plan_owned_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_stripe_customer_pricing_plan_owned_seq", sequenceName = "el_stripe_customer_pricing_plan_owned_seq", allocationSize = 50)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stripe_customer_id", nullable = false)
    private StripeCustomer stripeCustomer;

    @ManyToOne
    @JoinColumn(name = "stripe_pricing_plan_id", nullable = false)
    private StripePricingPlan stripePricingPlan;

}
