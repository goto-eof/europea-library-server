package com.andreidodu.europealibrary.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameDTO {

    @Size(min = 1, max = 100)
    private String oldName;

    @Size(min = 1, max = 100)
    private String newName;

}
