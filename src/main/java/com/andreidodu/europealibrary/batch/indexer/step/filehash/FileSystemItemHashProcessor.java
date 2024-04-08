package com.andreidodu.europealibrary.batch.indexer.step.filehash;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.util.FileUtil;
import com.andreidodu.europealibrary.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileSystemItemHashProcessor implements ItemProcessor<Long, FileSystemItem> {
    private final FileUtil fileUtil;
    private final FileSystemItemRepository fileSystemItemRepository;

    @Override
    public FileSystemItem process(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
        fileSystemItem = calculateAndUpdateHashAndMetaInfoIdIfNecessary(fileSystemItem);
        return fileSystemItem;
    }

    private FileSystemItem calculateAndUpdateHashAndMetaInfoIdIfNecessary(FileSystemItem fileSystemItem) {
        if (fileSystemItem.getIsDirectory()) {
            return fileSystemItem;
        }
        if (StringUtil.isNotEmpty(fileSystemItem.getSha256())) {
            return fileSystemItem;
        }

        return associateMetaInfoEntityIfFound(fileSystemItem);
    }


    private FileSystemItem associateMetaInfoEntityIfFound(FileSystemItem fileSystemItem) {
        final String fileFullPath = fileSystemItem.getBasePath() + "/" + fileSystemItem.getName();
        if (fileSystemItem.getSha256() == null) {
            fileSystemItem.setSha256(this.fileUtil.fileNameToHash(fileFullPath)
                    .orElse(null));
        }

        associateMetaInfoIfExists(fileSystemItem);

        return fileSystemItem;
    }

    private void associateMetaInfoIfExists(FileSystemItem fileSystemItem) {
        this.fileSystemItemRepository.findBySha256(fileSystemItem.getSha256())
                .stream()
                .filter(fsi -> fsi.getFileMetaInfo() != null)
                .findFirst()
                .flatMap(fsi -> Optional.ofNullable(fileSystemItem.getFileMetaInfo())
                        .map(FileMetaInfo::getId))
                .ifPresent(fileMetaInfoId -> this.fileSystemItemRepository.updateFileMetaInfoId(fileSystemItem.getId(), fileMetaInfoId));
    }


}
