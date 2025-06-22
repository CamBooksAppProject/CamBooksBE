package org.example.v1.post.image.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.v1.post.usedTrade.domain.UsedTrade;

@Entity
@Getter
@NoArgsConstructor
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl; // ex) /upload/used-trade/abcd.jpg

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_trade_id")
    private UsedTrade usedTrade;

    public PostImage(UsedTrade usedTrade, String imageUrl) {
        this.usedTrade = usedTrade;
        this.imageUrl = imageUrl;
    }
}
