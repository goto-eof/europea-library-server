package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.model.QTag;
import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.CustomTagRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomTagRepositoryImpl implements CustomTagRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Tag> retrieveTagsCursored(CommonCursoredRequestDTO commonCursoredRequestDTO) {

        Long cursorId = commonCursoredRequestDTO.getNextCursor();
        int numberOfResults = commonCursoredRequestDTO.getLimit() == null ? ApplicationConst.MAX_ITEMS_RETRIEVE : commonCursoredRequestDTO.getLimit();

        QTag tag = QTag.tag;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (cursorId != null) {
            booleanBuilder.and(tag.id.goe(cursorId));
        }

        if (numberOfResults > ApplicationConst.MAX_ITEMS_RETRIEVE) {
            numberOfResults = ApplicationConst.MAX_ITEMS_RETRIEVE;
        }

        return new JPAQuery<FileSystemItem>(entityManager)
                .select(tag)
                .from(tag)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .orderBy(tag.id.asc())
                .fetch();

    }
}
