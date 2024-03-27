package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.model.*;
import com.andreidodu.europealibrary.repository.CustomCategoryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Category> retrieveCategoriesCursored(CommonCursoredRequestDTO commonCursoredRequestDTO) {

        Long cursorId = commonCursoredRequestDTO.getNextCursor();
        int numberOfResults = commonCursoredRequestDTO.getLimit() == null ? ApplicationConst.MAX_ITEMS_RETRIEVE : commonCursoredRequestDTO.getLimit();

        QCategory category = QCategory.category;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (cursorId != null) {
            booleanBuilder.and(category.id.goe(cursorId));
        }

        if (numberOfResults > ApplicationConst.MAX_ITEMS_RETRIEVE) {
            numberOfResults = ApplicationConst.MAX_ITEMS_RETRIEVE;
        }

        return new JPAQuery<Category>(entityManager)
                .select(category)
                .from(category)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .orderBy(category.id.asc())
                .fetch();

    }
}
