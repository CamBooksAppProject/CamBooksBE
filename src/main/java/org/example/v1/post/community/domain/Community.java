package org.example.v1.post.community.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.v1.member.domain.Member;
import org.example.v1.post.domain.Post;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Community extends Post {
    @Enumerated(EnumType.STRING)
    private Region region;

    private String recruitment;  // 모집공고
    private String introduction; // 동아리 소개
    private Integer currentParticipants;
    private Integer maxParticipants;

    public Community(String title, Member writer, LocalDateTime createdAt,
                     Region region, String recruitment, String introduction, Integer maxParticipants) {
        super(title, writer, createdAt);
        this.region = region;
        this.recruitment = recruitment;
        this.introduction = introduction;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
    }
}
