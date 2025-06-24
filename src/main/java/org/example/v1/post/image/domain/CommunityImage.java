package org.example.v1.post.image.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.usedTrade.domain.UsedTrade;

@Entity
@Getter
@NoArgsConstructor
public class CommunityImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    public CommunityImage(Community community, String imageUrl) {
        this.community = community;
        this.imageUrl = imageUrl;
    }
}
