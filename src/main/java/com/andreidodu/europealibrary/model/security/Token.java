package com.andreidodu.europealibrary.model.security;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "el_token", uniqueConstraints = {@UniqueConstraint(columnNames = "user_id,token")})
@EntityListeners(AuditingEntityListener.class)
public class Token extends ModelCommon {

    @Id
    @GeneratedValue(generator = "el_token_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_token_seq", sequenceName = "el_token_seq", allocationSize = 50)
    private Long id;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "valid_flag")
    private Boolean validFlag = true;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
