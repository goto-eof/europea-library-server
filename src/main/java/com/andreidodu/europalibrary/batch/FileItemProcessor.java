package com.andreidodu.europalibrary.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileItemProcessor implements ItemProcessor<File, File> {
    @Override
    public File process(final File file) {
        System.out.println("File processed: "+file.getAbsoluteFile());
        return file;
    }

}
