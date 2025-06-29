package org.example.v1.comment.service;

import org.example.v1.comment.domain.Comment;
import org.example.v1.comment.domain.CommunityComment;
import org.example.v1.comment.dto.CommentRequestDto;
import org.example.v1.comment.dto.CommentResponseDto;
import org.example.v1.comment.repository.CommentRepository;
import org.example.v1.comment.repository.CommunityCommentRepository;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.community.repository.CommunityRepository;
import org.example.v1.post.generalForum.domain.GeneralForum;
import org.example.v1.post.generalForum.repository.GeneralForumRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class CommunityCommentService {
    private final CommunityCommentRepository communityCommentRepository;
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;

    public CommunityCommentService(CommunityCommentRepository communityCommentRepository, MemberRepository memberRepository, CommunityRepository communityRepository) {
        this.communityCommentRepository = communityCommentRepository;
        this.memberRepository = memberRepository;
        this.communityRepository = communityRepository;
    }
    public CommentResponseDto createComment(String email, Long postId, CommentRequestDto commentRequestDto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
        Community commu = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티 글을 찾을 수가 없습니다."));
        CommunityComment comment = CommunityComment.builder()
                .community(commu)
                .writer(member)
                .content(commentRequestDto.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        communityCommentRepository.save(comment);
        CommentResponseDto commentResponseDto = new CommentResponseDto(
                comment.getId(),
                comment.getWriter().getName(),
                comment.getContent(),
                communityCommentRepository.countByCommunity(commu),
                comment.getCreatedAt(),
                comment.getCommunity().getId()
        );
        return commentResponseDto;
    }
    public List<CommentResponseDto> getCommentList(Long postId) {
        Community community = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티 글을 찾을 수가 없습니다."));
        return communityCommentRepository.findByCommunity(community).stream()
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getWriter().getName(),
                        comment.getContent(),
                        communityCommentRepository.countByCommunity(community),
                        comment.getCreatedAt(),
                        comment.getCommunity().getId()
                ))
                .toList();
    }
    public Long countComment(Long postId) {
        Community community = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티 글을 찾을 수가 없습니다."));
        return communityCommentRepository.countByCommunity(community);
    }
    public List<CommentResponseDto> getMyComment(String email){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수가 없습니다."));
        return communityCommentRepository.findByWriter(member).stream()
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getWriter().getName(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getCommunity().getId()
                ))
                .toList();
    }
}
