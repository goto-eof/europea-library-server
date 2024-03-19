package com.andreidodu.europealibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OpenLibraryAuthenticationRequestDTO {
    private String access;
    private String secret;
}
