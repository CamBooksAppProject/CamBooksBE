package org.example.v1.post.usedTrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsedTradeResponseDto {
    private Long id;
    private String title;
    private String content;
    private int price;
    private String tradeMethod;
    private int viewCount;
    private String writerName;
    private String university;
    private String postType;
    private long postLikeCount;
}
