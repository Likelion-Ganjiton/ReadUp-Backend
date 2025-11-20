package com.springboot.readup.mypage.service;

import com.springboot.readup.mypage.dto.AlertRequestDto;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final UserRepository userRepository;

    // 현재 로그인 사용자 가져오기
    private UserEntity getCurrentUser() {
        String loginId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    // 알림 시간 조회
    public List<String> getAlertTimes() {
        UserEntity user = getCurrentUser();
        return user.getAlertTimes();
    }

    // 알림 시간 저장
    public void updateAlertTimes(AlertRequestDto requestDto) {
        UserEntity user = getCurrentUser();

        List<String> converted = requestDto.getAlertTimes().stream()
                .map(a -> a.getDay() + " " + a.getTime())
                .toList();

        user.setAlertTimes(new ArrayList<>(converted));

        userRepository.save(user);
    }
}