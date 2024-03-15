package com.andreidodu.europealibrary.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Component;

@Entity
@Getter
@Setter
@Table(name = "el_book_info", uniqueConstraints = {@UniqueConstraint(columnNames = {"file_meta_info_id", "authors", "publisher"})})
@EntityListeners(AuditingEntityListener.class)
public class BookInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String authors;

    @Column(length = 2000)
    private String note;

    @Column(length = 9)
    private String sbn;

    @Column(length = 13)
    private String isbn;

    @Column(length = 100)
    private String publisher;

    private Integer year;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_meta_info_id", referencedColumnName = "id")
    private FileMetaInfo fileMetaInfo;
}
