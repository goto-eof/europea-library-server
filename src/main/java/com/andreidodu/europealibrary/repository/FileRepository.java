package com.andreidodu.europealibrary.repository;

import java.io.File;
import java.util.List;

public interface FileRepository {
    List<File> getFilesInDirectoryPaginated(String directoryPath, int pageNumber);
}
