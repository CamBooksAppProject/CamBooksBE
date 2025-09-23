package org.example.v1.post.usedTrade.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.v1.member.domain.Member;
import org.example.v1.post.domain.Post;
import org.example.v1.post.image.domain.PostImage;
import org.example.v1.postLike.domain.PostType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class UsedTrade extends Post {
    @Column(length = 500)
    private String content;
    private Integer price;
    private String isbn;
    private Integer viewCount;

    @Enumerated(EnumType.STRING)
    private TradeMethod tradeMethod;

    public UsedTrade(String title, Member writer, LocalDateTime createdAt,
                     String content, Integer price, String isbn, Integer viewCount, TradeMethod tradeMethod) {
        super(title, writer, createdAt);
        this.content = content;
        this.price = price;
        this.isbn = isbn;
        this.viewCount = viewCount;
        this.tradeMethod = tradeMethod;
    }
    public void updateFields(String title, String content, Integer price, TradeMethod tradeMethod) {
        if (title != null) super.setTitle(title);
        if (content != null) this.content = content;
        if (price != null) this.price = price;
        if (tradeMethod != null) this.tradeMethod = tradeMethod;
    }
    @OneToMany(mappedBy = "usedTrade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @Override
    public PostType getPostType() {
        return PostType.USED_TRADE;
    }

    public void increaseView(){
        this.viewCount++;
    }
}
