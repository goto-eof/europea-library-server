package com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy;

import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagUtil {
    private final TagRepository tagRepository;

    private static Tag createTagFromName(String tag) {
        Tag tagEntity = new Tag();
        tagEntity.setName(tag);
        return tagEntity;
    }

    @Retryable(retryFor = {DataIntegrityViolationException.class, NullPointerException.class}, maxAttemptsExpression = "1000000")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Tag loadOrCreateTagEntity(String tagName) {
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
}
