package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.InputStreamResource;

@Getter
@Setter
public class DownloadDTO {
    private InputStreamResource inputStreamResource;
    private long fileSize;
    private String fileName;
}
