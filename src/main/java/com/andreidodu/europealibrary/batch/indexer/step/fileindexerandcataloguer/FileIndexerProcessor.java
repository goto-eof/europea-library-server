package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.batch.indexer.enums.RecordStatusEnum;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataretriever.MetaInfoRetrieverStrategy;
import com.andreidodu.europealibrary.batch.indexer.enums.ApiStatusEnum;
import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.mapper.FileMapper;
import com.andreidodu.europealibrary.mapper.FileSystemItemMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.util.EpubUtil;
import com.andreidodu.europealibrary.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileIndexerProcessor implements ItemProcessor<File, FileSystemItem> {
    public static final String DO_NOT_CALL_WEB_API = "do-not-call-web-api";
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.force-load-meta-info-from-web}")
    private boolean forceLoadMetaInfoFromWeb;
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.override-meta-info}")
    private boolean overrideMetaInfo;
    final private FileMapper fileMapper;
    final private FileUtil fileUtil;
    final private FileSystemItemMapper fileSystemItemMapper;
    final private FileSystemItemRepository fileSystemItemRepository;
    final private EpubUtil epubUtil;
    final private List<MetaInfoExtractorStrategy> metaInfoExtractorStrategyList;
    final private List<MetaInfoRetrieverStrategy> metaInfoRetrieverStrategyList;
    private StepExecution stepExecution;

    @BeforeStep
    public void setStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public FileSystemItem process(final File file) {
        log.info("Processing file: {}", file.getAbsoluteFile());
        // if job was stopped prematurely, then I have already a record on DB
        Optional<FileSystemItem> fileSystemIteminInsertedOptional = getFileSystemItemByPathNameAndJobStep(file.getParentFile().getAbsolutePath(), file.getName(), JobStepEnum.INSERTED.getStepNumber());
        if (fileSystemIteminInsertedOptional.isPresent()) {
            return recoverExistingFileSystemItem(fileSystemIteminInsertedOptional);
        }
        // case when file is in the same directory
        Optional<FileSystemItem> fileSystemItemInReadyOptional = getFileSystemItemByPathNameAndJobStep(file.getParentFile().getAbsolutePath(), file.getName(), JobStepEnum.READY.getStepNumber());
        if (fileSystemItemInReadyOptional.isPresent()) {
            return reprocessOldFileSystemItem(fileSystemItemInReadyOptional);
        }
        // case when file is new
        return buildFileSystemItemFromScratch(file);
    }

    private FileSystemItem reprocessOldFileSystemItem(Optional<FileSystemItem> fileSystemItemInReadyOptional) {
        FileSystemItem fileSystemItem = fileSystemItemInReadyOptional.get();
        fileSystemItem.setJobStep(JobStepEnum.INSERTED.getStepNumber());
        buildMetaInfoFromEbookIfNecessary(fileSystemItem);
        buildMetaInfoFromWebIfNecessary(fileSystemItem);
        fileSystemItem.setRecordStatus(RecordStatusEnum.JUST_UPDATED.getStatus());
        return fileSystemItem;
    }

    private FileSystemItem recoverExistingFileSystemItem(Optional<FileSystemItem> fileSystemIteminInsertedOptional) {
        FileSystemItem fileSystemItem = fileSystemIteminInsertedOptional.get();
        fileSystemItem.setRecordStatus(RecordStatusEnum.JUST_UPDATED.getStatus());
        buildMetaInfoFromEbookIfNecessary(fileSystemItem);
        buildMetaInfoFromWebIfNecessary(fileSystemItem);
        return fileSystemItem;
    }

    private FileSystemItem buildFileSystemItemFromScratch(File file) {
        try {
            FileDTO fileDTO = fileMapper.toDTO(file);
            log.info("building: " + fileDTO);
            return buildFileSystemItemFromScratch(fileDTO);
        } catch (IOException e) {
            log.error("Failed to process file: {}", file.getAbsoluteFile());
            return null;
        }
    }

    private FileSystemItem buildFileSystemItemFromScratch(FileDTO fileSystemItemDTO) {
        FileSystemItem fileSystemItem = this.fileSystemItemMapper.toModel(fileSystemItemDTO);
        fileSystemItem.setJobStep(JobStepEnum.INSERTED.getStepNumber());
        associateMetaInfoEntity(fileSystemItem);
        this.getFileSystemItemByPathNameAndJobStep(fileUtil.calculateParentBasePath(fileSystemItemDTO.getBasePath()), fileUtil.calculateParentName(fileSystemItemDTO.getBasePath()), JobStepEnum.INSERTED.getStepNumber())
                .ifPresentOrElse(fileSystemItem::setParent,
                        () -> this.getFileSystemItemByPathNameAndJobStep(fileUtil.calculateParentBasePath(fileSystemItemDTO.getBasePath()), fileUtil.calculateParentName(fileSystemItemDTO.getBasePath()), JobStepEnum.READY.getStepNumber())
                                .ifPresent(fileSystemItem::setParent)
                );
        fileSystemItem.setRecordStatus(RecordStatusEnum.JUST_UPDATED.getStatus());
        fileSystemItem.setExtension(this.fileUtil.getExtension(fileSystemItemDTO.getName()));
        return fileSystemItem;
    }

    private void associateMetaInfoEntity(FileSystemItem fileSystemItem) {
        if (fileSystemItem.getIsDirectory()) {
            return;
        }
        buildMetaInfoFromEbookIfNecessary(fileSystemItem);
        buildMetaInfoFromWebIfNecessary(fileSystemItem);
    }

    private void buildMetaInfoFromWebIfNecessary(FileSystemItem fileSystemItem) {
        metaInfoRetrieverStrategyList.stream()
                .filter(strategy -> strategy.accept(fileSystemItem))
                .findFirst()
                .ifPresent(strategy -> {
                    if (doNotCallApiIsTrue()) {
                        return;
                    }
                    ApiStatusEnum result = strategy.process(fileSystemItem);
                    if (result == ApiStatusEnum.FATAL_ERROR) {
                        this.stepExecution.getExecutionContext().put(DO_NOT_CALL_WEB_API, true);
                        return;
                    }
                    putThreadOnSleep();
                });
    }

    private boolean doNotCallApiIsTrue() {
        Object doNotCallApiIdTrue = stepExecution.getExecutionContext().get(DO_NOT_CALL_WEB_API);
        return doNotCallApiIdTrue != null && (boolean) doNotCallApiIdTrue;
    }

    private static void putThreadOnSleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("failed to put thread in sleep mode");
        }
    }

    private void buildMetaInfoFromEbookIfNecessary(FileSystemItem fileSystemItem) {
        String fullPath = fileSystemItem.getBasePath() + "/" + fileSystemItem.getName();
        log.info("checking for meta-info for file {}...", fullPath);
        this.metaInfoExtractorStrategyList
                .stream()
                .filter(strategy -> strategy.accept(fullPath, fileSystemItem))
                .findFirst()
                .map(strategy -> strategy.extract(fullPath, fileSystemItem))
                .flatMap(fileMetaInfo -> fileMetaInfo)
                .ifPresent(fileSystemItem::setFileMetaInfo);
    }


    private Optional<FileSystemItem> getFileSystemItemByPathNameAndJobStep(String basePath, String name, int jobStep) {
        return this.fileSystemItemRepository.findByBasePathAndNameAndJobStep(basePath, name, jobStep);
    }

}
