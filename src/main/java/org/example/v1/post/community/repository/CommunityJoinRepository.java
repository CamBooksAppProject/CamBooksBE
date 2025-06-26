package org.example.v1.post.community.repository;

import org.example.v1.member.domain.Member;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.community.domain.CommunityJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityJoinRepository extends JpaRepository<CommunityJoin, Long> {
    boolean existsByCommunityAndParticipant(Community community, Member participant);
}
