package org.example.v1.post.community.service;

import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.community.dto.CommunityRequestDto;
import org.example.v1.post.community.repository.CommunityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;

    public CommunityService(CommunityRepository communityRepository, MemberRepository memberRepository) {
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
    }
    public Community create(Long memberId, CommunityRequestDto dto) {
        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        Community post = new Community(
                dto.getTitle(),
                writer,
                LocalDateTime.now(),
                dto.getRegion(),
                dto.getRecruitment(),
                dto.getIntroduction(),
                dto.getMaxParticipants()
        );

        return communityRepository.save(post);
    }

    public List<Community> search(String keyword) {
        return communityRepository.findByTitleContaining(keyword);
    }

    public Community getById(Long postId) {
        return communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않음"));
    }
}

