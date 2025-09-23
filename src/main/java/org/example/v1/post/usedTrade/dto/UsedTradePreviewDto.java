package org.example.v1.post.usedTrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.v1.post.usedTrade.domain.TradeStatusType;

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
    private String thumbnailUrl;
    private TradeStatusType status;
}
