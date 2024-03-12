package com.andreidodu.europalibrary.batch;

import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.*;

@Component
@StepScope
public class FileItemReader implements ItemReader<File>, ItemStreamReader<File> {

    @Value("${com.andreidodu.europa-library.e-books-directory}")
    private String ebookDirectory;
    ListIterator<Path> iterator;
    List<Path> directories = new ArrayList<>();


    @PostConstruct
    public void postConstruct() {
        System.out.println(ebookDirectory);
        directories.add(Path.of(ebookDirectory));
    }

    @Override
    public File read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        System.out.println("READ()");
        if (iterator.hasNext()) {
            Path path = this.iterator.next();
            File file = path.toFile();
            if (file.isDirectory()) {
                System.out.println("is a directory....adding files...");
                Arrays.stream(Objects.requireNonNull(file.listFiles()))
                        .map(File::toPath)
                        .forEach(pathItem -> {
                            iterator.add(pathItem);
                            iterator.previous();
                        });
            }
            return file;
        }
        return null;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        DirectoryStream<Path> directoryStream = null;
        System.out.println("OPEN()");
        this.iterator = directories.listIterator();
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        ItemStreamReader.super.update(executionContext);
        System.out.println("UPDATE()");
    }

    @Override
    public void close() throws ItemStreamException {
        System.out.println("CLOSE()");
    }
}
