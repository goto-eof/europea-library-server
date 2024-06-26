package com.andreidodu.europealibrary.batch.indexer.step.fileindexer;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.batch.indexer.enums.RecordStatusEnum;
import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.mapper.FileMapper;
import com.andreidodu.europealibrary.mapper.FileSystemItemFullMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.util.FileUtil;
import com.andreidodu.europealibrary.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileIndexerProcessor implements ItemProcessor<File, FileSystemItem> {

    final private FileMapper fileMapper;
    final private FileUtil fileUtil;
    final private FileSystemItemFullMapper fileSystemItemFullMapper;
    final private FileSystemItemRepository fileSystemItemRepository;
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.force-load-meta-info-from-web}")
    private boolean forceLoadMetaInfoFromWeb;
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.override-meta-info}")
    private boolean overrideMetaInfo;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FileSystemItem process(final File file) {
        log.debug("Processing file: {}", file.getAbsoluteFile());
        List<FileSystemItem> fileSystemItemInReadyOptional = getFileSystemItemByPathNameAndJobStep(file.getParentFile().getAbsolutePath(), file.getName(), List.of(JobStepEnum.INSERTED.getStepNumber(), JobStepEnum.READY.getStepNumber()));
        return fileSystemItemInReadyOptional.stream().findFirst().map(/*case when file is in the same directory*/this::reprocessOldFileSystemItem)
                .orElseGet(() -> /*case when file is new*/ buildFileSystemItemFromScratch(file));

    }

    private FileSystemItem reprocessOldFileSystemItem(FileSystemItem fileSystemItem) {
        this.entityManager.detach(fileSystemItem);
        fileSystemItem.setJobStep(JobStepEnum.INSERTED.getStepNumber());
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
        FileSystemItem fileSystemItem = this.fileSystemItemFullMapper.toModel(fileSystemItemDTO);
        fileSystemItem.setJobStep(JobStepEnum.INSERTED.getStepNumber());
        fileSystemItem.setRecordStatus(RecordStatusEnum.JUST_UPDATED.getStatus());
        fileSystemItem.setExtension(StringUtil.toLowerCase(this.fileUtil.getExtension(fileSystemItemDTO.getName())));
        return fileSystemItem;
    }

    private List<FileSystemItem> getFileSystemItemByPathNameAndJobStep(String basePath, String name, List<Integer> jobSteps) {
        return this.fileSystemItemRepository.findByBasePathAndNameAndJobStepIn(basePath, name, jobSteps);
    }

}
