package com.springboot.readup.mypage.controller;

import com.springboot.readup.mypage.dto.AlertListResponseDto;
import com.springboot.readup.mypage.dto.AlertRequestDto;
import com.springboot.readup.mypage.dto.AlertResponseDto;
import com.springboot.readup.mypage.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class AlertController {

    private final AlertService alertService;

    @PatchMapping("/alert")
    public ResponseEntity<AlertResponseDto> updateAlertTimes(
            @RequestBody AlertRequestDto requestDto
    ) {
        alertService.updateAlertTimes(requestDto);
        return ResponseEntity.ok(new AlertResponseDto("알림 시간이 저장되었습니다."));
    }
    @GetMapping("/alert")
    public ResponseEntity<AlertListResponseDto> getAlertTimes() {
        return ResponseEntity.ok(
                new AlertListResponseDto(alertService.getAlertTimes())
        );
    }
}