package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetEmailRequestDTO {
    String username;
    String redirectTo;
}
