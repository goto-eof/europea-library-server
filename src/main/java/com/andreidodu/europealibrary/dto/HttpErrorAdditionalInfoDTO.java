package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.enums.InternalErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HttpErrorAdditionalInfoDTO extends HttpErrorDTO {
    private InternalErrorCodeEnum internalErrorCode;
    private String description;
}
