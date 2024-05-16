package com.andreidodu.europealibrary.model.stripe;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import com.andreidodu.europealibrary.model.security.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "el_stripe_customer_address")
@EntityListeners(AuditingEntityListener.class)
public class StripeCustomerAddress extends ModelCommon {

    @Id
    @GeneratedValue(generator = "el_stripe_customer_address_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_stripe_customer_address_seq", sequenceName = "el_stripe_customer_address_seq", allocationSize = 50)
    private Long id;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "line1")
    private String line1;

    @Column(name = "line2")
    private String line2;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "state")
    private String state;

    @ManyToOne
    @JoinColumn(name = "stripe_customer_id", unique = true, nullable = false)
    private StripeCustomer stripeCustomer;

}
