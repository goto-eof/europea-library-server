package com.andreidodu.europalibrary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FileDTO {
    private String filename;
    private double fileSize;
    private Date fileDate;
}
