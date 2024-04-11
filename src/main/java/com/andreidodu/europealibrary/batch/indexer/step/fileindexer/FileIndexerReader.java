package com.andreidodu.europealibrary.batch.indexer.step.fileindexer;

import com.andreidodu.europealibrary.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private final static ConcurrentLinkedQueue<File> directories = new ConcurrentLinkedQueue<>();

    @Override
    public File read() {
        if (!directories.isEmpty()) {
            File file = directories.poll();
            log.debug("processed: " + file.getAbsolutePath() + "/" + file.getName());
            return file;
        }
        log.debug("no more files to extract");
        return null;
    }

    private void loadDirectoryIfNecessary(File file) {
        if (file.isDirectory()) {
            log.debug("processing directory {}", file.getAbsolutePath());
            List<File> files = Arrays.stream(Objects.requireNonNull(file.listFiles()))
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
                    .toList();
            directories.addAll(files);
            files.forEach(this::loadDirectoryIfNecessary);
            log.debug("added {} children", files.size());
        }
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
        File file = Path.of(ebookDirectory).toFile();
        loadDirectoryIfNecessary(Objects.requireNonNull(file));
        directories.add(file);
    }

}
