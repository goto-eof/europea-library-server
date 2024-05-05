package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDTO {
    private Long id;
    private String identifier;
    private String title;
    private String subtitle;
    private String content;
}
