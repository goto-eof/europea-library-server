package com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy;

import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleStateException;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagUtil {
    private final TagRepository tagRepository;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    @Retryable(retryFor = {DataIntegrityViolationException.class, NullPointerException.class}, maxAttemptsExpression = "1000000" )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Tag createTagEntity(String tagName) {
        Optional<Tag> tagOptional = this.tagRepository.findByNameIgnoreCase(tagName);
        Tag tag;
        if (tagOptional.isEmpty()) {
            tag = createTagFromName(tagName);
            tag = this.tagRepository.save(tag);
            this.tagRepository.flush();
        } else {
            tag = tagOptional.get();
        }
        if (tag == null || tag.getId() == null) {
            throw new NullPointerException();
        }
        return tag;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    public Tag createTagEntityWorking(String tagName) {
        Optional<Tag> tagOptional = this.tagRepository.findByNameIgnoreCase(tagName);
        Tag tag;
        if (tagOptional.isEmpty()) {
            tag = createTagFromName(tagName);
            entityManager.persist(tag);
            tag = this.tagRepository.findByNameIgnoreCase(tagName).get();
            this.entityManager.lock(tag, LockModeType.PESSIMISTIC_WRITE);
            this.entityManager.flush();
        } else {
            tag = tagOptional.get(); //this.tagRepository.findByNameIgnoreCase(tagName).get();
        }
        return tag;
    }

    private static Tag createTagFromName(String tag) {
        Tag tagEntity = new Tag();
        tagEntity.setName(tag);
        return tagEntity;
    }
}
