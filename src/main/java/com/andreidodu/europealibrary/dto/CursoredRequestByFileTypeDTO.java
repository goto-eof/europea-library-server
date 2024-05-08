package com.andreidodu.europealibrary.dto;

import com.andreidodu.europealibrary.dto.common.Limitable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CursoredRequestByFileTypeDTO extends CommonCursoredRequestDTO {
    private String fileType;
}
