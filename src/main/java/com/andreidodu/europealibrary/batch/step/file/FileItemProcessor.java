package com.andreidodu.europealibrary.batch.step.file;

import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.mapper.FileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class FileItemProcessor implements ItemProcessor<File, FileDTO> {
    @Autowired
    private FileMapper fileMapper;

    @Override
    public FileDTO process(final File file) {
        log.info("File processed: {}", file.getAbsoluteFile());
        try {
            return fileMapper.toDTO(file);
        } catch (IOException e) {
            log.error("Failed to process file: {}", file.toString());
            throw new RuntimeException(e);
        }
    }


}
