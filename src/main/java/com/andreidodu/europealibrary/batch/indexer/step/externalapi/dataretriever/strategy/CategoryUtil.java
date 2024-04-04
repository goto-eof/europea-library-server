package com.andreidodu.europealibrary.batch.indexer.step.externalapi.dataretriever.strategy;

import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryUtil {
    private final CategoryRepository categoryRepository;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Category createCategoryEntity(String categoryName) {
        try {
            entityManager.createQuery("select l from Category l where lower(l.name) like lower(:name)", Category.class)
                    .setParameter("name", categoryName)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList().stream().findFirst()
                    .ifPresentOrElse(entityManager::persist
                            , () -> {
                                String trimed = StringUtil.cleanOrTrimToNull(categoryName);
                                if (trimed != null) {
                                    trimed = trimed.toLowerCase();
                                    entityManager.persist(createCategoryFromName(trimed));
                                }
                            });

        } catch (Exception e) {
            log.error("\n\n\n\nERROR: {}\n\n\n\n", e.getMessage());
        }
        return entityManager.createQuery("select l from Category l where lower(l.name) like lower(:name)", Category.class)
                .setParameter("name", categoryName)
                .setLockMode(LockModeType.NONE)
                .getResultList().stream().findFirst().get();
    }


    private static Category createCategoryFromName(String categoryName) {
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }
}
