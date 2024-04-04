package com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy;

import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.TagRepository;
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
public class TagUtil {
    private final TagRepository tagRepository;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Tag createTagEntity(String tagName) {
        try {
            entityManager.createQuery("select l from Tag l where lower(l.name) like lower(:name)", Tag.class)
                    .setParameter("name", tagName)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList().stream().findFirst()
                    .ifPresentOrElse(entityManager::persist
                            , () -> {
                                String trimed = StringUtil.cleanOrTrimToNull(tagName);
                                if (trimed != null) {
                                    trimed = trimed.toLowerCase();
                                    entityManager.persist(createTagFromName(trimed));
                                }
                            });
        } catch (Exception e) {
            log.error("\n\n\n\nERROR: {}\n\n\n\n", e.getMessage());
        }
        return entityManager.createQuery("select l from Tag l where lower(l.name) like lower(:name)", Tag.class)
                .setParameter("name", tagName)
                .setLockMode(LockModeType.NONE)
                .getResultList().stream().findFirst().get();
    }

    private static Tag createTagFromName(String tag) {
        Tag tagEntity = new Tag();
        tagEntity.setName(tag);
        return tagEntity;
    }
}
