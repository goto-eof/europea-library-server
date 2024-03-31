package com.andreidodu.europealibrary.batch.indexer.step.externalapi.dataretriever.strategy;

import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryUtil {
    private final CategoryRepository categoryRepository;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Category createAndAssociateCategories(Long fileMetaInfoId, String categoryName) {
        try {
            entityManager.createQuery("select l from Category l where lower(l.name) like lower(:name)", Category.class)
                    .setParameter("name", categoryName)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList().stream().findFirst()
                    .ifPresentOrElse(entityManager::persist
                            , () -> entityManager.persist(createCategory(categoryName)));

        } catch (Exception e) {
            log.error("\n\n\n\nERROR: {}\n\n\n\n", e.getMessage());
        }
        return entityManager.createQuery("select l from Category l where lower(l.name) like lower(:name)", Category.class)
                .setParameter("name", categoryName)
                .setLockMode(LockModeType.NONE)
                .getResultList().stream().findFirst().get();
    }


    private static Category createCategory(String categoryName) {
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }
}
