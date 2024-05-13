package com.andreidodu.europealibrary.model;

import com.andreidodu.europealibrary.model.common.Identifiable;
import com.andreidodu.europealibrary.model.common.ModelCommon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "el_tag")
@EntityListeners(AuditingEntityListener.class)
public class Tag extends ModelCommon implements Identifiable {
    @Id
    @GeneratedValue(generator = "el_tag_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "el_tag_seq", sequenceName = "el_tag_seq", allocationSize = 50)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @ManyToMany(mappedBy = "tagList", fetch = FetchType.LAZY)
    private List<FileMetaInfo> fileMetaInfoList = new ArrayList<>();

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
