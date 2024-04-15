package com.andreidodu.europealibrary.batch.indexer.step.tagwriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagWriterProcessor implements ItemProcessor<String, String> {
    @Override
    public String process(String tagName) {
        return tagName;
    }

}
