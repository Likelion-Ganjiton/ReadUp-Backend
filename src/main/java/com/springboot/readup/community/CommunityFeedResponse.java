package com.springboot.readup.community;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityFeedResponse {

    private String category;
    private List<CommunityDto> items;
}
