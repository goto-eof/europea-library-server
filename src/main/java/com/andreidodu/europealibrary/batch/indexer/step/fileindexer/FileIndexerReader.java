package com.andreidodu.europealibrary.batch.indexer.step.fileindexer;

import com.andreidodu.europealibrary.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    BlockingQueue<Path> directories = new LinkedBlockingQueue<>();

    @Override
    public File read() throws InterruptedException {
        if (!directories.isEmpty()) {
            Path path = directories.take();
            File file = path.toFile();
            if (file.isDirectory()) {
                log.debug("processing directory {}", file.getAbsolutePath());
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
                            directories.add(pathItem);
                            log.debug("added child {}", pathItem.toFile().getAbsolutePath());
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
        log.debug("ebooks directory: " + ebookDirectory);
        directories.add(Path.of(ebookDirectory));
    }

}
