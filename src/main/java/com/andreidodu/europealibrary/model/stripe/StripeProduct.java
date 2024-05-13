package com.andreidodu.europealibrary.model.stripe;

import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "el_stripe_product")
@EntityListeners(AuditingEntityListener.class)
public class StripeProduct extends ModelCommon {

    @Id
    @GeneratedValue(generator = "el_stripe_product_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_stripe_product_seq", sequenceName = "el_stripe_product_seq", allocationSize = 50)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "stripe_product_id")
    private String stripeProductId;

    @OneToOne
    @JoinColumn(name = "file_meta_info_id", referencedColumnName = "id", unique = true, nullable = false)
    private FileMetaInfo fileMetaInfo;

    @OneToOne(mappedBy = "stripeProduct", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private StripePrice stripePrice;


}
