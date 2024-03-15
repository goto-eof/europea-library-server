package com.andreidodu.europealibrary.model;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "el_file_meta_info")
@EntityListeners(AuditingEntityListener.class)
public class FileMetaInfo extends ModelCommon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512)
    private String title;

    @Column(length = 4000)
    private String description;

    @OneToOne(mappedBy = "fileMetaInfo", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private BookInfo bookInfo;


    @ManyToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            uniqueConstraints = {@UniqueConstraint(columnNames = {"file_meta_info_id", "tag_id"})},
            name = "el_file_meta_info_tag",
            joinColumns = {@JoinColumn(name = "file_meta_info_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private List<Tag> tagList;

    @OneToMany(mappedBy = "fileMetaInfo")
    @Fetch(FetchMode.JOIN)
    private List<FileSystemItem> fileSystemItemList;
}
