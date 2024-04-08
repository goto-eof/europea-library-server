package com.andreidodu.europealibrary.batch.indexer.step.fileindexer;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.batch.indexer.enums.RecordStatusEnum;
import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.mapper.FileMapper;
import com.andreidodu.europealibrary.mapper.FileSystemItemMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.util.FileUtil;
import com.andreidodu.europealibrary.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.force-load-meta-info-from-web}")
    private boolean forceLoadMetaInfoFromWeb;
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.override-meta-info}")
    private boolean overrideMetaInfo;
    final private FileMapper fileMapper;
    final private FileUtil fileUtil;
    final private FileSystemItemMapper fileSystemItemMapper;
    final private FileSystemItemRepository fileSystemItemRepository;


    @Override
    public FileSystemItem process(final File file) {
        log.debug("Processing file: {}", file.getAbsoluteFile());
        // if job was stopped prematurely, then I have already a record on DB
        Optional<FileSystemItem> fileSystemIteminInsertedOptional = getFileSystemItemByPathNameAndJobStep(file.getParentFile().getAbsolutePath(), file.getName(), JobStepEnum.INSERTED.getStepNumber());
        if (fileSystemIteminInsertedOptional.isPresent()) {
            return recoverExistingFileSystemItem(fileSystemIteminInsertedOptional.get());
        }
        Optional<FileSystemItem> fileSystemItemInReadyOptional = getFileSystemItemByPathNameAndJobStep(file.getParentFile().getAbsolutePath(), file.getName(), JobStepEnum.READY.getStepNumber());
        return fileSystemItemInReadyOptional.map(/*case when file is in the same directory*/this::reprocessOldFileSystemItem)
                .orElseGet(() -> /*case when file is new*/ buildFileSystemItemFromScratch(file));

    }

    private FileSystemItem reprocessOldFileSystemItem(FileSystemItem fileSystemItem) {
        fileSystemItem.setJobStep(JobStepEnum.INSERTED.getStepNumber());
        fileSystemItem.setRecordStatus(RecordStatusEnum.JUST_UPDATED.getStatus());
        return fileSystemItem;
    }

//    private void updateParentIdIfNull(FileSystemItem fileSystemItem) {
//        Optional<FileSystemItem> parentOptional = Optional.ofNullable(fileSystemItem.getParent());
//        if (parentOptional.isEmpty()) {
//            List<Integer> validSteps = List.of(JobStepEnum.INSERTED.getStepNumber(), JobStepEnum.READY.getStepNumber());
//            List<FileSystemItem> parents = this.getFileSystemItemByPathNameAndJobStepInList(fileUtil.calculateParentBasePath(fileSystemItem.getBasePath()), fileUtil.calculateFileName(fileSystemItem.getBasePath()), validSteps);
//            if (!parents.isEmpty()) {
//                fileSystemItem.setParent(parents.stream().findFirst().get());
//            }
//        }
//    }

    private FileSystemItem recoverExistingFileSystemItem(FileSystemItem fileSystemItem) {
        fileSystemItem.setRecordStatus(RecordStatusEnum.JUST_UPDATED.getStatus());
        return fileSystemItem;
    }

    private FileSystemItem buildFileSystemItemFromScratch(File file) {
        try {
            FileDTO fileDTO = fileMapper.toDTO(file);
            log.debug("building: " + fileDTO);
            return buildFileSystemItemFromScratch(fileDTO);
        } catch (IOException e) {
            log.error("Failed to process file: {}", file.getAbsoluteFile());
            return null;
        }
    }

    private FileSystemItem buildFileSystemItemFromScratch(FileDTO fileSystemItemDTO) {
        FileSystemItem fileSystemItem = this.fileSystemItemMapper.toModel(fileSystemItemDTO);
        fileSystemItem.setJobStep(JobStepEnum.INSERTED.getStepNumber());
//        this.getFileSystemItemByPathNameAndJobStep(fileUtil.calculateParentBasePath(fileSystemItemDTO.getBasePath()), fileUtil.calculateFileName(fileSystemItemDTO.getBasePath()), JobStepEnum.INSERTED.getStepNumber())
//                .ifPresentOrElse(fileSystemItem::setParent,
//                        () -> this.getFileSystemItemByPathNameAndJobStep(fileUtil.calculateParentBasePath(fileSystemItemDTO.getBasePath()), fileUtil.calculateFileName(fileSystemItemDTO.getBasePath()), JobStepEnum.READY.getStepNumber())
//                                .ifPresent(fileSystemItem::setParent)
//                );
        fileSystemItem.setRecordStatus(RecordStatusEnum.JUST_UPDATED.getStatus());
        fileSystemItem.setExtension(StringUtil.toLowerCase(this.fileUtil.getExtension(fileSystemItemDTO.getName())));
        return fileSystemItem;
    }

    private Optional<FileSystemItem> getFileSystemItemByPathNameAndJobStep(String basePath, String name, int jobStep) {
        return this.fileSystemItemRepository.findByBasePathAndNameAndJobStep(basePath, name, jobStep);
    }

//    private List<FileSystemItem> getFileSystemItemByPathNameAndJobStepInList(String basePath, String name, List<Integer> jobStepList) {
//        return this.fileSystemItemRepository.findByBasePathAndNameAndJobStepIn(basePath, name, jobStepList);
//    }

}
