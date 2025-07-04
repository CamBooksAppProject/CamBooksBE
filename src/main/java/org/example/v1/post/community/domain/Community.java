package org.example.v1.post.community.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.v1.member.domain.Member;
import org.example.v1.post.domain.Post;
import org.example.v1.post.image.domain.CommunityImage;
import org.example.v1.post.image.domain.PostImage;
import org.example.v1.postLike.domain.PostType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Community extends Post {
    @Enumerated(EnumType.STRING)
    private Region region;

    @Column(length = 500)
    private String recruitment;  // 모집공고
    @Column(length = 500)
    private String introduction; // 동아리 소개

    private Integer currentParticipants;
    private Integer maxParticipants;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;


    public Community(String title, Member writer, LocalDateTime createdAt,
                     Region region, String recruitment, String introduction, Integer maxParticipants, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(title, writer, createdAt);
        this.region = region;
        this.recruitment = recruitment;
        this.introduction = introduction;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityImage> images = new ArrayList<>();

    @Override
    public PostType getPostType() {
        return PostType.COMMUNITY;
    }
    public void incrementCurrentParticipants() {
        this.currentParticipants++;
    }
    public void decrementCurrentParticipants() {
        this.currentParticipants--;
    }
    public void updateField(String title, Region region, String recruitment, String introduction, Integer maxParticipants, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super.setTitle(title);
        this.region = region;
        this.recruitment = recruitment;
        this.introduction = introduction;
        this.maxParticipants = maxParticipants;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}
