package com.andreidodu.europealibrary.model.auth;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "el_user", uniqueConstraints = {@UniqueConstraint(columnNames = "username,email")})
@EntityListeners(AuditingEntityListener.class)
public class User extends ModelCommon {

    @Id
    @GeneratedValue(generator = "el_user_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_user_seq", sequenceName = "el_user_seq", allocationSize = 50)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @NonNull
    @JsonIgnore
    private String password;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "recovery_key")
    private String recoveryKey;

    @Column(name = "recovery_expiration")
    private LocalDateTime recoveryExpiration;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST})
    private List<Authority> authorityList;

}
