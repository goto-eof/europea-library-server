package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.batch.indexer.enums.ApiStatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseDTO<T> {
    private T entity;
    private ApiStatusEnum status;
}
