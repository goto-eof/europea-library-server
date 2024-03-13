package com.andreidodu.europealibrary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JobStatusDTO {
    private boolean isRunning;
    private String message;
}
