package com.springboot.readup.news.dto;

import lombok.Data;
import java.util.List;

@Data
public class AIHubNewsResponse {
    private AIHubNewsHeader header;
    private List<AIHubNewsItem> text;
}
