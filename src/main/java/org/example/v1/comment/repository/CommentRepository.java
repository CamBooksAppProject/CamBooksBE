package org.example.v1.comment.repository;

import org.example.v1.comment.domain.Comment;
import org.example.v1.member.domain.Member;
import org.example.v1.post.generalForum.domain.GeneralForum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByWriter(Member writer);
    List<Comment> findByGeneralForum(GeneralForum generalForum);
    Long countByGeneralForum(GeneralForum generalForum);
    void deleteAllByWriter(Member writer);
}
