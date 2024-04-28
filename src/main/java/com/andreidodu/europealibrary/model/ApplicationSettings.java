package com.andreidodu.europealibrary.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "el_application_settings")
public class ApplicationSettings {

    public ApplicationSettings(Long id, Boolean applicationLock) {
        this.id = id;
        this.applicationLock = applicationLock;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_lock")
    private Boolean applicationLock;

}