package com.andreidodu.europealibrary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationStatusDTO {
    private boolean status;
    private String message;

    public OperationStatusDTO(final boolean status) {
        this.status = status;
    }
}
