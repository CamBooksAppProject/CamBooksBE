package org.example.v1.post.usedTrade.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.v1.member.domain.Member;
import org.example.v1.post.domain.Post;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class UsedTrade extends Post {
    private String content;
    private Integer price;
    private Integer viewCount;

    @Enumerated(EnumType.STRING)
    private TradeMethod tradeMethod;

    public UsedTrade(String title, Member writer, LocalDateTime createdAt,
                     String content, Integer price, Integer viewCount, TradeMethod tradeMethod) {
        super(title, writer, createdAt);
        this.content = content;
        this.price = price;
        this.viewCount = viewCount;
        this.tradeMethod = tradeMethod;
    }
    public void updateFields(String title, String content, Integer price, TradeMethod tradeMethod) {
        if (title != null) super.setTitle(title);
        if (content != null) this.content = content;
        if (price != null) this.price = price;
        if (tradeMethod != null) this.tradeMethod = tradeMethod;
    }
}
