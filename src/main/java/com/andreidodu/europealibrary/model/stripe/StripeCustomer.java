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
@Table(name = "el_stripe_customer")
@EntityListeners(AuditingEntityListener.class)
public class StripeCustomer extends ModelCommon {

    @Id
    @GeneratedValue(generator = "el_stripe_customer_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_stripe_customer_seq", sequenceName = "el_stripe_customer_seq", allocationSize = 50)
    private Long id;

    @Column(name = "stripe_customer_id", unique = true)
    private String stripeCustomerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "current_stripe_customer_address_id", unique = true, nullable = false)
    private StripeCustomerAddress currentStripeCustomerAddress;

    @OneToMany(mappedBy = "stripeCustomer")
    private List<StripeCustomerProductsOwned> stripeCustomerProductsOwnedList;

    @OneToMany(mappedBy = "stripeCustomer")
    private List<StripeCustomerAddress> stripeCustomerAddressList;
}
