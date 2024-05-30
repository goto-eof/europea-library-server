package com.andreidodu.europealibrary.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEnableDTO {
    @NotNull
    private Boolean enabled;
}
