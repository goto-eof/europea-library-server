package com.andreidodu.europealibrary.model;

import com.andreidodu.europealibrary.model.common.Identificable;
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
@Table(name = "el_category")
@EntityListeners(AuditingEntityListener.class)
public class Category extends ModelCommon implements Identificable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @ManyToMany(mappedBy = "categoryList")
    @Fetch(FetchMode.JOIN)
    private List<BookInfo> bookInfoList;

}
