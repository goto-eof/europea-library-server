package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.constants.ApplicationConst;
import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.PaginatedExplorerOptions;
import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.model.QBookInfo;
import com.andreidodu.europealibrary.model.QCategory;
import com.andreidodu.europealibrary.model.QFileMetaInfo;
import com.andreidodu.europealibrary.repository.CustomCategoryRepository;
import com.andreidodu.europealibrary.repository.common.CommonRepository;
import com.andreidodu.europealibrary.util.LimitUtil;
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
public class CustomCategoryRepositoryImpl extends CommonRepository implements CustomCategoryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Category> retrieveCategoriesCursored(PaginatedExplorerOptions paginatedExplorerOptions, CommonCursoredRequestDTO commonCursoredRequestDTO) {

        Long cursorId = commonCursoredRequestDTO.getNextCursor();

        int numberOfResults = LimitUtil.calculateLimit(commonCursoredRequestDTO, ApplicationConst.CATEGORIES_MAX_ITEMS_RETRIEVE);

        QCategory category = QCategory.category;
        QBookInfo bookInfo = QBookInfo.bookInfo;
        QFileMetaInfo fileMetaInfo = QFileMetaInfo.fileMetaInfo;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        applyCommonFilter(fileMetaInfo, paginatedExplorerOptions, booleanBuilder);

        Optional.ofNullable(cursorId)
                .ifPresent(id -> booleanBuilder.and(category.id.goe(id)));

        return new JPAQuery<Category>(entityManager)
                .select(category)
                .from(category)
                .innerJoin(category.bookInfoList, bookInfo)
                .innerJoin(bookInfo.fileMetaInfo, fileMetaInfo)
                .where(booleanBuilder)
                .limit(numberOfResults + 1)
                .fetch();

    }
}
