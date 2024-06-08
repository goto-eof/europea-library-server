package com.andreidodu.europealibrary.model.stripe;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "el_stripe_price")
@EntityListeners(AuditingEntityListener.class)
public class StripePrice extends ModelCommon {

    @Id
    @GeneratedValue(generator = "el_stripe_product_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_stripe_product_seq", sequenceName = "el_stripe_product_seq", allocationSize = 50)
    private Long id;

    @Column(name = "stripe_price_id")
    private String stripePriceId;

    @Column(name = "currency")
    private String currency;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "archived")
    private Boolean archived;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stripe_product_id", nullable = false)
    private StripeProduct stripeProduct;

    @OneToMany(mappedBy = "stripePrice", fetch = FetchType.LAZY)
    private List<StripePurchaseSession> stripePurchaseSessionHistoryList;

}
