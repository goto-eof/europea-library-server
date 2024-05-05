package com.andreidodu.europealibrary.model;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "el_post")
@EntityListeners(AuditingEntityListener.class)
public class Post extends ModelCommon {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(generator = "el_post_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_post_seq", sequenceName = "el_post_seq", allocationSize = 50)
    private Long id;

    @Column(name = "identifier", length = 100, nullable = false, unique = true)
    private String identifier;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "subtitle", length = 100)
    private String subtitle;

    @Column(name = "content", length = 1000, nullable = false)
    private String content;

}
