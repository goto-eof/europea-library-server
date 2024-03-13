package com.andreidodu.europealibrary.repository.impl;

import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.repository.FileRepository;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class FileRepositoryImpl implements FileRepository {

    public static final long ITEMS_PER_PAGE = 10L;

    @Override
    public List<File> getFilesInDirectoryPaginated(final String directoryPath, final int pageNumber) {
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath));
            Spliterator<Path> splitIterator = directoryStream.spliterator();
            List<File> result = StreamSupport.stream(splitIterator, false)
                    .skip(pageNumber * ITEMS_PER_PAGE)
                    .limit(ITEMS_PER_PAGE)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            directoryStream.close();
            return result;
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}
