package com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Order(1000)
@Component
@Transactional
@RequiredArgsConstructor
public class OtherMetaInfoExtractorStrategyImpl implements MetaInfoExtractorStrategy {
    private final static String STRATEGY_NAME = "file-meta-info-other-extractor";
    private final FileUtil fileUtil;

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public boolean accept(String filename, FileSystemItem fileSystemItem) {
        return fileSystemItem == null || fileSystemItem.getFileMetaInfo() == null || fileSystemItem.getFileMetaInfo().getBookInfo() == null;
    }

    @Override
    public Optional<FileMetaInfo> extract(String filename, FileSystemItem fileSystemItem) {
        FileMetaInfo fileMetaInfo = new FileMetaInfo();
        fileMetaInfo.setTitle(fileUtil.calculateFileName(filename));
        return Optional.of(fileMetaInfo);
    }
}
