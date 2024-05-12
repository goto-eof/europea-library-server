package com.andreidodu.europealibrary.model.stripe;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import com.andreidodu.europealibrary.model.security.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "el_stripe_customer")
@EntityListeners(AuditingEntityListener.class)
public class StripeCustomer extends ModelCommon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stripe_customer_id", unique = true)
    private String stripeCustomerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true, nullable = false)
    private User user;

}
