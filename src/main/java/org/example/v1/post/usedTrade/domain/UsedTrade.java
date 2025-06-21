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
    private int price;
    private int viewCount;

    @Enumerated(EnumType.STRING)
    private TradeMethod tradeMethod;

    public UsedTrade(String title, Member writer, LocalDateTime createdAt,
                     String content, int price, int viewCount, TradeMethod tradeMethod) {
        super(title, writer, createdAt);
        this.content = content;
        this.price = price;
        this.viewCount = viewCount;
        this.tradeMethod = tradeMethod;
    }
}
