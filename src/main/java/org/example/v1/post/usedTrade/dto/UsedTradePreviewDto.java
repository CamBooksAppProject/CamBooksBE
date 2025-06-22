package org.example.v1.post.usedTrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsedTradePreviewDto {
    private Long id;
    private String title;
    private int price;
    private int viewCount;
    private String university;
    private long postLikeCount;
}
