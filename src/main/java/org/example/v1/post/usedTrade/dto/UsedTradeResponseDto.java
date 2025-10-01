package org.example.v1.post.usedTrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.v1.post.usedTrade.domain.TradeStatusType;
import org.example.v1.post.usedTrade.domain.UsedTradeStatus;

import java.util.List;

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
    private List<String> imageUrls;
    private Long userId;
    private TradeStatusType status;
    private String isbn;

    public UsedTradeResponseDto(Long id, String title, String content, int price, String tradeMethod, int viewCount, String writerName, String university, String postType, long postLikeCount, Long userId , TradeStatusType status, String isbn) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.price = price;
        this.tradeMethod = tradeMethod;
        this.viewCount = viewCount;
        this.writerName = writerName;
        this.university = university;
        this.postType = postType;
        this.postLikeCount = postLikeCount;
        this.userId = userId;
        this.status = status;
        this.isbn = isbn;
    }
}
