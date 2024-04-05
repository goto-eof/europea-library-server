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
public class FileSystemItemHashProcessor implements ItemProcessor<FileSystemItem, FileSystemItem> {
    private final FileUtil fileUtil;
    private final FileSystemItemRepository fileSystemItemRepository;

    @Override
    public FileSystemItem process(FileSystemItem fileSystemItem) {
        calculateAndUpdateHashAndMetaInfoIdIfNecessary(fileSystemItem);
        log.info("updated hash for {} record: {}", 1, fileSystemItem.getName());
        this.fileSystemItemRepository.flush();
        return null;
    }

    private void calculateAndUpdateHashAndMetaInfoIdIfNecessary(FileSystemItem fileSystemItem) {
        if (fileSystemItem.getIsDirectory()) {
            return;
        }
        if (StringUtil.isNotEmpty(fileSystemItem.getSha256())) {
            return;
        }
        final String fileFullPath = fileSystemItem.getBasePath() + "/" + fileSystemItem.getName();
        String sha256 = this.fileUtil.fileNameToHash(fileFullPath)
                .orElse(null);
        associateMetaInfoEntityIfFound(fileSystemItem, sha256);
    }

    private void associateMetaInfoEntityIfFound(FileSystemItem fileSystemItem, String sha256) {
        if (fileSystemItem.getIsDirectory()) {
            return;
        }
        if (StringUtil.isEmpty(sha256)) {
            return;
        }
        associateMetaInfoByHashIfFound(fileSystemItem.getId(), sha256);
    }

    private void associateMetaInfoByHashIfFound(Long fileSystemItemId, String sha256) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
        Optional.ofNullable(fileSystemItem.getSha256())
                .flatMap(hash -> this.fileSystemItemRepository.findBySha256(hash)
                        .stream()
                        .filter(fsi -> fsi.getFileMetaInfo() != null)
                        .findFirst().flatMap(fsi -> Optional.ofNullable(fileSystemItem.getFileMetaInfo())
                                .map(FileMetaInfo::getId)))
                .ifPresentOrElse(fileMetaInfoId ->
                        this.fileSystemItemRepository.updateFileMetaInfoId(fileSystemItem.getId(), fileMetaInfoId), () -> {
                    fileSystemItem.setSha256(sha256);
                    this.fileSystemItemRepository.save(fileSystemItem);
                });
    }


}
