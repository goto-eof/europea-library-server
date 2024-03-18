package com.andreidodu.europealibrary.batch.indexer.step.file;

import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.mapper.FileMapper;
import com.andreidodu.europealibrary.util.FileUtil;
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
    @Autowired
    private FileUtil fileUtil;

    @Override
    public FileDTO process(final File file) {
        log.info("File processed: {}", file.getAbsoluteFile());
        try {
            FileDTO dto = fileMapper.toDTO(file);
            if (file.isFile()) {
                dto.setSha256(this.fileUtil.fileToSha256(file));
            }
            return dto;
        } catch (IOException e) {
            log.error("Failed to process file: {}", file.toString());
            throw new RuntimeException(e);
        }
    }

}
