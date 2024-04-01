package com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.TagRepository;
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
public class TagUtil {
    private final TagRepository tagRepository;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Tag createTag(String tag) {
        try {
            entityManager.createQuery("select l from Tag l where lower(l.name) like lower(:name)", Tag.class)
                    .setParameter("name", tag)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList().stream().findFirst()
                    .ifPresentOrElse(entityManager::persist
                            , () -> entityManager.persist(createTagFromName(tag)));
        } catch (Exception e) {
            log.error("\n\n\n\nERROR: {}\n\n\n\n", e.getMessage());
        }
        return entityManager.createQuery("select l from Tag l where lower(l.name) like lower(:name)", Tag.class)
                .setParameter("name", tag)
                .setLockMode(LockModeType.NONE)
                .getResultList().stream().findFirst().get();
    }

    private static Tag createTagFromName(String tag) {
        Tag tagEntity = new Tag();
        tagEntity.setName(tag);
        return tagEntity;
    }
}
