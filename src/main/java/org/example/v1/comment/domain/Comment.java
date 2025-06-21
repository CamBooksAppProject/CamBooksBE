package org.example.v1.comment.domain;

import jakarta.persistence.*;
import org.example.v1.member.domain.Member;
import org.example.v1.post.domain.Post;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Member writer;

    private String content;
}
