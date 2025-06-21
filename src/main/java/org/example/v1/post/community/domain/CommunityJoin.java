package org.example.v1.post.community.domain;

import jakarta.persistence.*;
import org.example.v1.member.domain.Member;

@Entity
public class CommunityJoin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Community community;

    @ManyToOne
    private Member participant;
}
