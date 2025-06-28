package org.example.v1.postLike.repository;

import org.example.v1.member.domain.Member;
import org.example.v1.post.domain.Post;
import org.example.v1.postLike.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByMemberAndPost(Member member, Post post);
    long countByPost(Post post);
    void deleteByMemberAndPost(Member member, Post post);
    void deleteAllByMember(Member member);
    List<PostLike> findAllByMember(Member member);
}
