package org.example.v1.comment.repository;

import org.example.v1.comment.domain.Comment;
import org.example.v1.comment.domain.CommunityComment;
import org.example.v1.member.domain.Member;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.generalForum.domain.GeneralForum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findByWriter(Member writer);
    List<CommunityComment> findByCommunity(Community community);
    Long countByCommunity(Community community);
    void deleteAllByWriter(Member writer);
}
