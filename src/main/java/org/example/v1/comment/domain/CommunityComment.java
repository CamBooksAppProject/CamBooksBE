package org.example.v1.comment.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.v1.member.domain.Member;
import org.example.v1.post.community.domain.Community;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Community community;
    @ManyToOne
    private Member writer;

    private String content;

    private LocalDateTime createdAt;

}
