package org.example.v1.post.usedTrade.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.v1.post.usedTrade.domain.UsedTradeStatus;

@Getter
@NoArgsConstructor
public class UsedTradeStatusDto {
    private Long usedTradeId;
    private String title;
    private Integer price;
    private String writerName;
    private String buyerName;
    private String universityName;
    private String status;
    private String thumbnailUrl;

    public UsedTradeStatusDto(UsedTradeStatus status, String thumbnailUrl) {
        this.usedTradeId = status.getUsedTrade().getId();
        this.title = status.getUsedTrade().getTitle();
        this.price = status.getUsedTrade().getPrice();
        this.writerName = status.getUsedTrade().getWriter().getName();
        this.buyerName = status.getBuyer() != null ? status.getBuyer().getName() : null;
        this.universityName = status.getUsedTrade().getWriter().getUniversity().getNameKo();
        this.status = status.getStatus().name();
        this.thumbnailUrl = thumbnailUrl;
    }
}
