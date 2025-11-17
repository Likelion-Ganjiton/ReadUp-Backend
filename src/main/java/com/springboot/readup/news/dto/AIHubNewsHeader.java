package com.springboot.readup.news.dto;

import lombok.Data;

@Data
public class AIHubNewsHeader {
    private String identifier;
    private String name;
    private int category;
    private int type;
    private String source_file;
    private String source;
}