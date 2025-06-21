package org.example.v1.post.generalForum.service;

import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.generalForum.domain.GeneralForum;
import org.example.v1.post.generalForum.dto.GeneralForumRequestDto;
import org.example.v1.post.generalForum.repository.GeneralForumRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GeneralForumService {
    private final GeneralForumRepository generalForumRepository;
    private final MemberRepository memberRepository;

    public GeneralForumService(GeneralForumRepository generalForumRepository, MemberRepository memberRepository) {
        this.generalForumRepository = generalForumRepository;
        this.memberRepository = memberRepository;
    }
    public GeneralForum create(Long memberId, GeneralForumRequestDto dto){
        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        GeneralForum post = new GeneralForum(
                dto.getTitle(),
                writer,
                LocalDateTime.now(),
                dto.getContent()
        );

        return generalForumRepository.save(post);
    }
    public List<GeneralForum> search(String keyword) {
        return generalForumRepository.findByTitleContaining(keyword);
    }

    public GeneralForum getById(Long postId) {
        return generalForumRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않음"));
    }
}
