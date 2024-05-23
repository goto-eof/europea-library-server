package com.andreidodu.europealibrary.model;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "el_featured_file_meta_info")
@EntityListeners(AuditingEntityListener.class)
public class FeaturedFileMetaInfo extends ModelCommon {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(generator = "el_featured_file_meta_info_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_featured_file_meta_info_seq", sequenceName = "el_featured_file_meta_info_seq", allocationSize = 50)
    private Long id;

    @OneToOne
    @JoinColumn(name = "file_meta_info_id", referencedColumnName = "id")
    private FileMetaInfo fileMetaInfo;

}
