package com.andreidodu.europealibrary.batch.step.file;

import com.andreidodu.europealibrary.batch.JobStepEnum;
import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.mapper.FileSystemItemMapper;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class FileItemWriter implements ItemWriter<FileDTO> {
    private final FileSystemItemRepository fileSystemItemRepository;
    private final FileSystemItemMapper fileSystemItemMapper;

    @Override
    public void write(Chunk<? extends FileDTO> chunk) {
        chunk.getItems().forEach(fileSystemItemDTO -> {
            Optional<FileSystemItem> fileSystemItemOptional = getParentFileSystemItem(fileSystemItemDTO.getBasePath(), fileSystemItemDTO.getName(), JobStepEnum.INSERTED.getStepNumber());
            if (fileSystemItemOptional.isEmpty()) {
                createAndSaveFileSystemItem(fileSystemItemDTO);
                log.info("INSERT: " + fileSystemItemDTO);
            }
        });
    }

    private void createAndSaveFileSystemItem(FileDTO fileSystemItemDTO) {
        FileSystemItem fileSystemItem = buildFileSystemItem(fileSystemItemDTO);
        fileSystemItemRepository.save(fileSystemItem);
    }

    private FileSystemItem buildFileSystemItem(FileDTO fileSystemItemDTO) {
        FileSystemItem model = this.fileSystemItemMapper.toModel(fileSystemItemDTO);
        model.setJobStep(JobStepEnum.INSERTED.getStepNumber());
        this.getParentFileSystemItem(calculateParentBasePath(fileSystemItemDTO.getBasePath()), calculateParentName(fileSystemItemDTO.getBasePath()), JobStepEnum.INSERTED.getStepNumber())
                .ifPresent(model::setParent);
        return model;
    }

    private String calculateParentName(String basePath) {
        return new File(basePath).getName();
    }

    private String calculateParentBasePath(String basePath) {
        return new File(basePath).getParentFile().getAbsolutePath();
    }

    private Optional<FileSystemItem> getParentFileSystemItem(String basePath, String name, int jobStep) {
        return this.fileSystemItemRepository.findByBasePathAndNameAndJobStep(basePath, name, jobStep);
    }
}
