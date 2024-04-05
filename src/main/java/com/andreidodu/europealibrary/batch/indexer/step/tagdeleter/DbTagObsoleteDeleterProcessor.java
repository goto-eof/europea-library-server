package com.andreidodu.europealibrary.batch.indexer.step.tagdeleter;

import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbTagObsoleteDeleterProcessor implements ItemProcessor<Long, Tag> {
    private final TagRepository tagRepository;

    @Override
    public Tag process(Long tagId) {
        return this.tagRepository.findById(tagId).get();
    }
}
