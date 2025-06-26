package org.example.v1.post.community.service;

import jakarta.transaction.Transactional;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.community.domain.CommunityJoin;
import org.example.v1.post.community.repository.CommunityJoinRepository;
import org.example.v1.post.community.repository.CommunityRepository;
import org.springframework.stereotype.Service;

@Service
public class CommunityJoinService {

    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;
    private final CommunityJoinRepository communityJoinRepository;

    public CommunityJoinService(CommunityRepository communityRepository, MemberRepository memberRepository, CommunityJoinRepository communityJoinRepository) {
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
        this.communityJoinRepository = communityJoinRepository;
    }

    @Transactional
    public Integer joinCommunity(String email, Long communityId) {
        Member participant = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티가 존재하지 않습니다."));

        if (communityJoinRepository.existsByCommunityAndParticipant(community, participant)) {
            throw new IllegalStateException("이미 신청한 커뮤니티입니다.");
        }

        if (community.getCurrentParticipants() >= community.getMaxParticipants()) {
            throw new IllegalStateException("모집 인원이 가득 찼습니다.");
        }

        CommunityJoin join = new CommunityJoin(null, community, participant);
        communityJoinRepository.save(join);
        community.incrementCurrentParticipants();
        return community.getCurrentParticipants();
    }
}
