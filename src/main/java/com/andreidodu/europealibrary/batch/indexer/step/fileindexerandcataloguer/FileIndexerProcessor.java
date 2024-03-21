package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer;

import com.andreidodu.europealibrary.batch.indexer.JobStepEnum;
import com.andreidodu.europealibrary.batch.indexer.RecordStatusEnum;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataretriever.MetaInfoRetrieverStrategy;
import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.mapper.FileMapper;
import com.andreidodu.europealibrary.mapper.FileSystemItemMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.util.EpubUtil;
import com.andreidodu.europealibrary.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class FileIndexerProcessor implements ItemProcessor<File, FileSystemItem> {
    @Value("${com.andreidodu.europa-library.force-load-meta-info-from-web}")
    private boolean forceLoadMetaInfoFromWeb;
    @Value("${com.andreidodu.europa-library.override-meta-info}")
    private boolean overrideMetaInfo;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private FileSystemItemMapper fileSystemItemMapper;
    @Autowired
    private FileSystemItemRepository fileSystemItemRepository;
    @Autowired
    private EpubUtil epubUtil;
    @Autowired
    private List<MetaInfoExtractorStrategy> metaInfoExtractorStrategyList;

    @Autowired
    List<MetaInfoRetrieverStrategy> metaInfoRetrieverStrategyList;


    @Override
    public FileSystemItem process(final File file) {
        log.info("Processing file: {}", file.getAbsoluteFile());
        // if job was stopped prematurely, then I have already a record on DB
        Optional<FileSystemItem> fileSystemIteminInsertedOptional = getFileSystemItemByPathNameAndJobStep(file.getParentFile().getAbsolutePath(), file.getName(), JobStepEnum.INSERTED.getStepNumber());
        if (fileSystemIteminInsertedOptional.isPresent()) {
            fileSystemIteminInsertedOptional.get().setRecordStatus(RecordStatusEnum.JUST_UPDATED.getStatus());
            return null;
        }
        // case when file is in the same directory
        Optional<FileSystemItem> fileSystemItemInReadyOptional = getFileSystemItemByPathNameAndJobStep(file.getParentFile().getAbsolutePath(), file.getName(), JobStepEnum.READY.getStepNumber());
        if (fileSystemItemInReadyOptional.isPresent()) {
            FileSystemItem fileSystemItem = fileSystemItemInReadyOptional.get();
            fileSystemItem.setJobStep(JobStepEnum.INSERTED.getStepNumber());
            buildMetaInfoFromEbookIfNecessary(fileSystemItem);
            buildMetaInfoFromWebIfNecessary(fileSystemItem);
            fileSystemItem.setRecordStatus(RecordStatusEnum.JUST_UPDATED.getStatus());
            return fileSystemItem;
        }
        // case when fle is new
        return buildFileSystemItem(file);
    }

    private FileSystemItem buildFileSystemItem(File file) {
        try {
            FileDTO fileDTO = fileMapper.toDTO(file);
            log.info("building: " + fileDTO);
            return buildFileSystemItem(fileDTO);
        } catch (IOException e) {
            log.error("Failed to process file: {}", file.getAbsoluteFile());
            return null;
        }
    }

    private FileSystemItem buildFileSystemItem(FileDTO fileSystemItemDTO) {
        FileSystemItem model = this.fileSystemItemMapper.toModel(fileSystemItemDTO);
        model.setJobStep(JobStepEnum.INSERTED.getStepNumber());
        associateMetaInfoEntity(model);
        this.getFileSystemItemByPathNameAndJobStep(fileUtil.calculateParentBasePath(fileSystemItemDTO.getBasePath()), fileUtil.calculateParentName(fileSystemItemDTO.getBasePath()), JobStepEnum.INSERTED.getStepNumber())
                .ifPresent(model::setParent);
        model.setRecordStatus(RecordStatusEnum.JUST_UPDATED.getStatus());
        return model;
    }

    private void associateMetaInfoEntity(FileSystemItem model) {
        if (model.getIsDirectory()) {
            return;
        }
        if (!overrideMetaInfo && loadMetaInfoFromDB(model)) {
            return;
        }
        if (buildMetaInfoFromEbookIfNecessary(model) && !forceLoadMetaInfoFromWeb) {
            return;
        }
        buildMetaInfoFromWebIfNecessary(model);
    }

    private boolean buildMetaInfoFromWebIfNecessary(FileSystemItem fileSystemItem) {
        return metaInfoRetrieverStrategyList.stream()
                .filter(strategy -> strategy.accept(fileSystemItem))
                .findFirst()
                .map(strategy -> {
                    strategy.process(fileSystemItem);
                    putThreadOnSleep();
                    return true;
                })
                .orElse(false);
    }

    private static void putThreadOnSleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("failed to put thread in sleep mode");
        }
    }

    private boolean loadMetaInfoFromDB(FileSystemItem model) {
        List<FileSystemItem> oldItems = this.fileSystemItemRepository.findBySha256AndJobStep(model.getSha256(), JobStepEnum.READY.getStepNumber());
        if (!oldItems.isEmpty() && oldItems.stream().anyMatch(oldItem -> oldItem.getFileMetaInfo() != null)) {
            model.setFileMetaInfo(oldItems.getFirst().getFileMetaInfo());
            return true;
        }
        return false;
    }

    private boolean buildMetaInfoFromEbookIfNecessary(FileSystemItem model) {
        String fullPath = model.getBasePath() + "/" + model.getName();
        log.info("checking for meta-info for file {}...", fullPath);
        return this.metaInfoExtractorStrategyList
                .stream()
                .filter(strategy -> strategy.accept(fullPath, model))
                .findFirst()
                .map(strategy -> strategy.extract(fullPath, model))
                .flatMap(item -> item)
                .map(metaInfo -> {
                    model.setFileMetaInfo(metaInfo);
                    List<FileSystemItem> list = new ArrayList<>();
                    list.add(model);
                    metaInfo.setFileSystemItemList(list);
                    return metaInfo;
                }).isPresent();
    }


    private Optional<FileSystemItem> getFileSystemItemByPathNameAndJobStep(String basePath, String name, int jobStep) {
        return this.fileSystemItemRepository.findByBasePathAndNameAndJobStep(basePath, name, jobStep);
    }

}
