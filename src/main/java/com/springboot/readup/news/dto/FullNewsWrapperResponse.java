package com.springboot.readup.news.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FullNewsWrapperResponse {

    private int status;
    private String message;
    private FullNewsData data;

    @Getter
    @Builder
    public static class FullNewsData {
        private List<FullNewsResponse> newsList;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }
}