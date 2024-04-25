package com.andreidodu.europealibrary.model.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "el_role", uniqueConstraints = {@UniqueConstraint(columnNames = "user_id,role_name")})
public class Role {

    @Id
    @GeneratedValue(generator = "el_role_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_role_seq", sequenceName = "el_role_seq", allocationSize = 50)
    private Long id;

    @Column(name = "role_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
