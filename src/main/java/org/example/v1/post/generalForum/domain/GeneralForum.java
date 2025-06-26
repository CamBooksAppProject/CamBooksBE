package org.example.v1.post.generalForum.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.v1.member.domain.Member;
import org.example.v1.post.domain.Post;
import org.example.v1.postLike.domain.PostType;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class GeneralForum extends Post {
    @Column(length = 500)
    private String content;

    public GeneralForum(String title, Member writer, LocalDateTime createdAt, String content) {
        super(title, writer, createdAt);
        this.content = content;
    }

    @Override
    public PostType getPostType() {
        return PostType.GENERAL_FORUM;
    }
}
