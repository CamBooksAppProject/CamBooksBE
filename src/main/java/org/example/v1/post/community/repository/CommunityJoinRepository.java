package org.example.v1.post.community.repository;

import org.example.v1.member.domain.Member;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.community.domain.CommunityJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityJoinRepository extends JpaRepository<CommunityJoin, Long> {
    boolean existsByCommunityAndParticipant(Community community, Member participant);
    void deleteAllByParticipant(Member participant);
    Optional<CommunityJoin> findByCommunityAndParticipant(Community community, Member participant);
    List<CommunityJoin> findByCommunity(Community community);
}
