package com.springboot.readup.news.controller;

import com.springboot.readup.news.service.NewsPayloadFixService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/news")
public class NewsPayloadFixController {

    private final NewsPayloadFixService fixService;

    @PostMapping("/fix-payload")
    public String fix() {
        fixService.fixPayloads();
        return "OK";
    }
}
