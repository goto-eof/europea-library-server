package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenLibraryAuthenticationRequestDTO {
    private String access;
    private String secret;
}
