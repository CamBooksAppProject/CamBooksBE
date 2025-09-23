package org.example.v1.post.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.v1.member.domain.Member;
import org.example.v1.postLike.domain.PostType;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "post_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable=false)
    private Member writer;

    private LocalDateTime createdAt;

    protected Post(String title, Member writer, LocalDateTime createdAt) {
        this.title = title;
        this.writer = writer;
        this.createdAt = createdAt;
    }
    protected void setTitle(String title) {
        this.title = title;
    }
    public abstract PostType getPostType();
}
