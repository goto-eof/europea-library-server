package com.andreidodu.europealibrary.batch.indexer.step.categorywriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryWriterProcessor implements ItemProcessor<String, String> {
    @Override
    public String process(String categoryName) {
        return categoryName;
    }

}
