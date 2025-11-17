package com.springboot.readup.like;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeToggle {

    private boolean liked;

    private long likeCount;
}
