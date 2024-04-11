package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.model.QCategory;
import com.andreidodu.europealibrary.repository.CustomCategoryRepository;
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
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Category> retrieveCategoriesCursored(CommonCursoredRequestDTO commonCursoredRequestDTO) {

        Long cursorId = commonCursoredRequestDTO.getNextCursor();

        int numberOfResults = Optional.ofNullable(commonCursoredRequestDTO.getLimit())
                .orElse(ApplicationConst.FILE_SYSTEM_EXPLORER_MAX_ITEMS_RETRIEVE);

        QCategory category = QCategory.category;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        Optional.ofNullable(cursorId)
                .ifPresent(id -> booleanBuilder.and(category.id.goe(id)));

        if (numberOfResults > ApplicationConst.CATEGORIES_MAX_ITEMS_RETRIEVE) {
            numberOfResults = ApplicationConst.CATEGORIES_MAX_ITEMS_RETRIEVE;
        }

        return new JPAQuery<Category>(entityManager)
                .select(category)
                .from(category)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .fetch();

    }
}
