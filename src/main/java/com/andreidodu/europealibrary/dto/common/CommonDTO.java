package com.andreidodu.europealibrary.dto.common;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommonDTO {

    protected LocalDateTime createdDate;
    protected LocalDateTime lastModifiedDate;
}
