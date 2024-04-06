package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer;

import com.andreidodu.europealibrary.util.FileUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class FileIndexerReader implements ItemStreamReader<File> {

    @Value("${com.andreidodu.europea-library.job.indexer.e-books-directory}")
    private String ebookDirectory;
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.skip-file-extensions}")
    private List<String> fileExtensionsToIgnore;

    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.allow-file-extensions}")
    private List<String> fileExtensionsToAllow;

    private final FileUtil fileUtil;
    ListIterator<Path> iterator;
    List<Path> directories = new ArrayList<>();


    @PostConstruct
    public void postConstruct() {
        log.debug("ebooks directory: " + ebookDirectory);
        directories.add(Path.of(ebookDirectory));
    }

    @Override
    public File read() {
        if (iterator.hasNext()) {
            Path path = this.iterator.next();
            File file = path.toFile();
            if (file.isDirectory()) {
                Arrays.stream(Objects.requireNonNull(file.listFiles()))
                        .peek(fileItem -> {
                            if (!fileExtensionsToIgnore.isEmpty() && fileItem.isFile() && fileExtensionsToIgnore.contains(fileUtil.getExtension(fileItem.getName()).toLowerCase())) {
                                log.debug("ignoring file: {}", fileItem.getAbsolutePath() + "/" + fileItem.getName());
                            }
                        })
                        .peek(fileItem -> {
                            if (!fileExtensionsToAllow.isEmpty() && fileItem.isFile() && fileExtensionsToAllow.contains(fileUtil.getExtension(file.getName()).toLowerCase())) {
                                log.debug("the following file will be processed: {}", fileItem.getAbsolutePath() + "/" + fileItem.getName());
                            }
                        })
                        .filter(fileItem -> fileExtensionsToIgnore.isEmpty() || fileItem.isDirectory() || !fileExtensionsToIgnore.contains(fileUtil.getExtension(fileItem.getName()).toLowerCase()))
                        .filter(fileItem -> fileExtensionsToAllow.isEmpty() || fileItem.isDirectory() || fileExtensionsToAllow.contains(fileUtil.getExtension(fileItem.getName()).toLowerCase()))
                        .sorted(sortByIsDirectoryAndName())
                        .map(File::toPath)
                        .forEach(pathItem -> {
                            iterator.add(pathItem);
                            iterator.previous();
                        });
            }
            log.debug("found: " + file.getAbsolutePath() + "/" + file.getName());
            return file;
        }
        return null;
    }

    private static Comparator<File> sortByIsDirectoryAndName() {
        return (Comparator
                .comparing(File::isDirectory)
                .reversed()
                .thenComparing(File::getName)
                .reversed());
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
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
