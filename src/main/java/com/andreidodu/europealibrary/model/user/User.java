package com.andreidodu.europealibrary.model.user;

import jakarta.persistence.*;

@Entity
@Table(name = "el_user", uniqueConstraints = {@UniqueConstraint(columnNames = "username,email")})
public class User {

    @Id
    @GeneratedValue(generator = "el_user_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_user_seq", sequenceName = "el_user_seq", allocationSize = 50)
    private Long id;

    private String username;
    private String email;
    private String password;

}
