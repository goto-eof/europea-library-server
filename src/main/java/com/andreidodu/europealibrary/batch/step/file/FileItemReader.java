package com.andreidodu.europealibrary.batch.step.file;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Component
@StepScope
public class FileItemReader implements ItemStreamReader<File> {

    @Value("${com.andreidodu.europa-library.e-books-directory}")
    private String ebookDirectory;
    ListIterator<Path> iterator;
    List<Path> directories = new ArrayList<>();


    @PostConstruct
    public void postConstruct() {
        log.info("ebooks directory: " + ebookDirectory);
        directories.add(Path.of(ebookDirectory));
    }

    @Override
    public File read() {
        if (iterator.hasNext()) {
            Path path = this.iterator.next();
            File file = path.toFile();
            if (file.isDirectory()) {
                Arrays.stream(Objects.requireNonNull(file.listFiles()))
                        .map(File::toPath)
                        .forEach(pathItem -> {
                            iterator.add(pathItem);
                            iterator.previous();
                        });
            }
            log.info("found: " + file.getAbsolutePath() + " | " + file.getName());
            return file;
        }
        return null;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        DirectoryStream<Path> directoryStream = null;
        this.iterator = directories.listIterator();
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        ItemStreamReader.super.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
    }
}
