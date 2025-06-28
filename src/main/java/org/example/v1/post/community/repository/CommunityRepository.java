package org.example.v1.post.community.repository;

import org.example.v1.member.domain.Member;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.repository.PostRepository;

import java.util.List;

public interface CommunityRepository extends PostRepository<Community> {
    List<Community> findByTitleContaining(String keyword);
    void deleteAllByWriter(Member writer);
}
