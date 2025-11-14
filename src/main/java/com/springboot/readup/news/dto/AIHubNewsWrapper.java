package com.springboot.readup.news.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AIHubNewsWrapper {

    @JsonProperty("SJML")
    private AIHubNewsResponse SJML;
}