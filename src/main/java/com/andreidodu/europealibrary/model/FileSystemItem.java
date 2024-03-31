package com.andreidodu.europealibrary.model;

import com.andreidodu.europealibrary.mapper.FileMetaInfoMapper;
import com.andreidodu.europealibrary.model.common.Identificable;
import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "el_file_system_item", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "base_path", "job_step"})})
@EntityListeners(AuditingEntityListener.class)
public class FileSystemItem extends ModelCommon implements Identificable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String name;

    @Column(nullable = false)
    private Long size;

    @Column(name = "base_path", nullable = false)
    private String basePath;

    @Column(name = "sha256")
    private String sha256;

    @Column(name = "file_create_date", nullable = false)
    private LocalDateTime fileCreateDate;

    @Column(name = "file_update_date", nullable = false)
    private LocalDateTime fileUpdateDate;

    @Column(name = "is_directory")
    private Boolean isDirectory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private FileSystemItem parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE})
    private List<FileSystemItem> childrenList;

    @Column(name = "job_step", nullable = false)
    private Integer jobStep;

    @Column(name = "job_status")
    private Integer jobStatus;

    private String extension;

    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "file_meta_info_id", nullable = true, insertable = false, updatable = false)
    private FileMetaInfo fileMetaInfo;

    @Column(name = "file_meta_info_id", nullable = true)
    private Long fileMetaInfoId;

    @Column(name = "record_status")
    private Integer recordStatus;

}
