package com.andreidodu.europealibrary.dto.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonDTO {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createdDate;
    protected LocalDateTime lastModifiedDate;
    private Long id;
}
