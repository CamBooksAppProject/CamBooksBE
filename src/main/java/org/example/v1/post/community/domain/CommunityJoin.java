package org.example.v1.post.community.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.v1.member.domain.Member;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommunityJoin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Community community;

    @ManyToOne
    private Member participant;
}
