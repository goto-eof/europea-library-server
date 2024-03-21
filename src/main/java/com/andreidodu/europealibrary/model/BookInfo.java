package com.andreidodu.europealibrary.model;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "el_book_info", uniqueConstraints = {@UniqueConstraint(columnNames = {"file_meta_info_id", "authors", "publisher"})})
@EntityListeners(AuditingEntityListener.class)
public class BookInfo extends ModelCommon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String authors;

    @Column(length = 2000)
    private String note;

    @Column(length = 10)
    private String isbn10;

    @Column(length = 13)
    private String isbn13;

    @Column(length = 100)
    private String publisher;

    @Column(length = 10)
    private String language;

    @Column(name = "num_pages")
    private Integer numberOfPages;

    @Column(name = "published_date", length = 50)
    private String publishedDate;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "ratings_count")
    private Long ratingsCount;

    @Column(name = "is_info_extracted_from_file")
    private Boolean isInfoExtractedFromFile;

    @Column(name = "is_info_retrieved_from_web")
    private Boolean isInfoRetrievedFromWeb;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "file_meta_info_id", referencedColumnName = "id")
    private FileMetaInfo fileMetaInfo;

    @Override
    public String toString() {
        return "BookInfo{" +
                "authors='" + authors + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", publisher='" + publisher + '\'' +
                '}';
    }
}
