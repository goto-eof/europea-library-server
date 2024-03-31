package com.andreidodu.europealibrary.batch.indexer.step.filehash;

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
public class FileSystemItemHashProcessor implements ItemProcessor<FileSystemItem, FileSystemItem> {
    private final FileUtil fileUtil;
    private final FileSystemItemRepository fileSystemItemRepository;

    @Override
    public FileSystemItem process(FileSystemItem fileSystemItem) {
        calculateAndUpdateHashAndMetaInfoIdIfNecessary(fileSystemItem);
        return fileSystemItem;
    }

    private void calculateAndUpdateHashAndMetaInfoIdIfNecessary(FileSystemItem fileSystemItem) {
        if (fileSystemItem.getIsDirectory()) {
            return;
        }
        if (StringUtil.isNotEmpty(fileSystemItem.getSha256())) {
            return;
        }
        final String fileFullPath = fileSystemItem.getBasePath() + "/" + fileSystemItem.getName();
        this.fileUtil.fileNameToHash(fileFullPath)
                .ifPresent(fileSystemItem::setSha256);
        fileSystemItem = this.fileSystemItemRepository.save(fileSystemItem);
        associateMetaInfoEntityIfFound(fileSystemItem);
    }

    private void associateMetaInfoEntityIfFound(FileSystemItem fileSystemItem) {
        if (fileSystemItem.getIsDirectory()) {
            return;
        }
        if (StringUtil.isEmpty(fileSystemItem.getSha256())) {
            return;
        }
        associateMetaInfoByHashIfFound(fileSystemItem);
    }

    private void associateMetaInfoByHashIfFound(FileSystemItem fileSystemItem) {
        Optional.ofNullable(fileSystemItem.getSha256())
                .flatMap(hash -> this.fileSystemItemRepository.findBySha256(hash)
                        .stream()
                        .filter(fsi -> fsi.getFileMetaInfo() != null)
                        .findFirst()
                        .map(fsi -> fileSystemItem.getFileMetaInfo().getId()))
                .ifPresent(fileMetaInfoId -> this.fileSystemItemRepository.updateFileMetaInfoId(fileSystemItem.getId(), fileMetaInfoId));
    }


}
