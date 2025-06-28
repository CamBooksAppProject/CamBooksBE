package org.example.v1.post.generalForum.repository;

import org.example.v1.member.domain.Member;
import org.example.v1.post.generalForum.domain.GeneralForum;
import org.example.v1.post.repository.PostRepository;

import java.util.List;

public interface GeneralForumRepository extends PostRepository<GeneralForum> {
    List<GeneralForum> findByTitleContaining(String keyword);
    void deleteAllByWriter(Member writer);
    //검색
    List<GeneralForum> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword);

}
