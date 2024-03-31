package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.strategy;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContexts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TagUtil {
    private final TagRepository tagRepository;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createAndAssociateTags(FileMetaInfo fileMetaInfo, String tag) {
        Optional<Tag> tagOptional = Optional.empty();
        try {
            tagOptional = this.tagRepository.findByNameIgnoreCase(tag);
            tagOptional = Optional.of(tagOptional.orElse(this.tagRepository.saveAndFlush(createTag(tag))));
        } catch (Exception e) {
            tagOptional = this.tagRepository.findByNameIgnoreCase(tag);
        }
        this.associateTags(fileMetaInfo, tagOptional.get());
    }

    private void associateTags(FileMetaInfo fileMetaInfo, Tag tag) {
        if (fileMetaInfo.getTagList() == null) {
            fileMetaInfo.setTagList(new ArrayList<>());
        }
        if (tag.getFileMetaInfoList() == null) {
            tag.setFileMetaInfoList(new ArrayList<>());
        }
        fileMetaInfo.getTagList().add(tag);
        tag.getFileMetaInfoList().add(fileMetaInfo);
        this.fileMetaInfoRepository.save(fileMetaInfo);
    }

    private static Tag createTag(String tag) {
        Tag tagEntity = new Tag();
        tagEntity.setName(tag);
        return tagEntity;
    }
}
