package com.andreidodu.europealibrary.model;

import com.andreidodu.europealibrary.model.common.ModelCommon;
import com.andreidodu.europealibrary.model.security.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "el_temporary_resource_identifier")
@EntityListeners(AuditingEntityListener.class)
public class TemporaryResourceIdentifier extends ModelCommon {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(generator = "el_tmp_res_identifier_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_tmp_res_identifier_seq", sequenceName = "el_tmp_res_identifier_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_system_item_id", nullable = false)
    private FileSystemItem fileSystemItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "temporary_resource_identifier", length = 100, nullable = false, unique = true)
    private String identifier;

    @Column(name = "valid_from_ts")
    protected LocalDateTime validFromTs;

    @Column(name = "valid_to_ts")
    protected LocalDateTime validToTs;

}
