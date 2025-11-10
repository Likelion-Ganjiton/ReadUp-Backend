package com.springboot.readup.user.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserSignupRequest {
    private String loginId;
    private String password;
    private String email;
    private String name;
    private String nickname;
    private List<String> categories;
}