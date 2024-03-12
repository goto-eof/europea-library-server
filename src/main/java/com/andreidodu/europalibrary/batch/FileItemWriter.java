package com.andreidodu.europalibrary.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileItemWriter implements ItemWriter<File> {
    @Override
    public void write(Chunk<? extends File> chunk) throws Exception {
        System.out.println("Writing chunks");
        chunk.getItems().stream().forEach(file -> System.out.println(file.getName()));
    }
}
