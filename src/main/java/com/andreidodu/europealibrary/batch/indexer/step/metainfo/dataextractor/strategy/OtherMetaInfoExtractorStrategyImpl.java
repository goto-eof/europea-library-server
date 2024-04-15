package com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.constants.DataPropertiesConst;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.BookInfoRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.util.FileUtil;
import com.andreidodu.europealibrary.util.StringUtil;
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
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final BookInfoRepository bookInfoRepository;

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public boolean accept(String fullPathAndName, FileSystemItem fileSystemItem) {
        return !fileUtil.isDirectory(fullPathAndName) && (fileSystemItem == null || fileSystemItem.getFileMetaInfo() == null || fileSystemItem.getFileMetaInfo().getBookInfo() == null);
    }

    @Override
    public Optional<FileMetaInfo> extract(String filename, FileSystemItem fileSystemItem) {
        return this.extract(filename, fileSystemItem, false);
    }

    public Optional<FileMetaInfo> extract(String filename, FileSystemItem fileSystemItem, boolean isCorrupted) {
        FileMetaInfo oldFileMetaInfo = fileSystemItem.getFileMetaInfo();
        FileMetaInfo fileMetaInfo = oldFileMetaInfo == null ? new FileMetaInfo() : oldFileMetaInfo;

        Optional.ofNullable(StringUtil.cleanAndTrimToNullSubstring(fileUtil.calculateFileName(filename), DataPropertiesConst.FILE_META_INFO_TITLE_MAX_LENGTH))
                .ifPresentOrElse(fileMetaInfo::setTitle, () -> fileMetaInfo.setTitle("_NO_TITLE_"));

        FileMetaInfo savedFileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);

        BookInfo oldBookInfo = fileMetaInfo.getBookInfo();
        BookInfo bookInfo = oldBookInfo == null ? new BookInfo() : oldBookInfo;
        bookInfo.setIsCorrupted(isCorrupted);
        bookInfo.setFileMetaInfo(savedFileMetaInfo);
        this.bookInfoRepository.save(bookInfo);

        return Optional.of(fileMetaInfo);
    }
}
