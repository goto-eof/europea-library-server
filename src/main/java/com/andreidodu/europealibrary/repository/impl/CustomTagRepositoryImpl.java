package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
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
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomTagRepositoryImpl implements CustomTagRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Tag> retrieveTagsCursored(CommonCursoredRequestDTO commonCursoredRequestDTO) {

        Long cursorId = commonCursoredRequestDTO.getNextCursor();

        int numberOfResults = Optional.ofNullable(commonCursoredRequestDTO.getLimit())
                .orElse(ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QTag tag = QTag.tag;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        Optional.ofNullable(cursorId)
                .ifPresent(id -> booleanBuilder.and(tag.id.goe(id)));

        if (numberOfResults > ApplicationConst.TAGS_MAX_ITEMS_RETRIEVE) {
            numberOfResults = ApplicationConst.TAGS_MAX_ITEMS_RETRIEVE;
        }

        return new JPAQuery<Tag>(entityManager)
                .select(tag)
                .from(tag)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .fetch();

    }
}
