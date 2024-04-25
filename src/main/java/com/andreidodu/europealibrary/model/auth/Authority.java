package com.andreidodu.europealibrary.model.auth;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "el_authority_user", uniqueConstraints = {@UniqueConstraint(columnNames = "user_id,role_name")})
@EntityListeners(AuditingEntityListener.class)
public class Authority extends ModelCommon {

    @Id
    @GeneratedValue(generator = "el_authority_user_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_authority_user_seq", sequenceName = "el_authority_user_seq", allocationSize = 50)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
