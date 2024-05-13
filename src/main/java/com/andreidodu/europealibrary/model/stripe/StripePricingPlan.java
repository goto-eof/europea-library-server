package com.andreidodu.europealibrary.model.stripe;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.common.ModelCommon;
import com.stripe.param.PlanCreateParams;
import com.stripe.param.PriceCreateParams;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "el_stripe_pricing_plan")
@EntityListeners(AuditingEntityListener.class)
public class StripePricingPlan extends ModelCommon {

    @Id
    @GeneratedValue(generator = "el_stripe_pricing_plan_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_stripe_pricing_plan_seq", sequenceName = "el_stripe_pricing_plan_seq", allocationSize = 50)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "interval")
    private PriceCreateParams.Recurring.Interval interval;

    @Column(name = "stripe_pricing_plan_id")
    private String stripePricingPlanId;

    @OneToMany(mappedBy = "stripePricingPlan")
    private List<StripePurchaseSession> stripePurchaseSessionList;

}
