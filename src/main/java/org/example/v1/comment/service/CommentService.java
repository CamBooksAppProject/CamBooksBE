package org.example.v1.comment.service;

import org.example.v1.comment.repository.CommentRepository;
import org.example.v1.comment.domain.Comment;
import org.example.v1.comment.dto.CommentRequestDto;
import org.example.v1.comment.dto.CommentResponseDto;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.generalForum.domain.GeneralForum;
import org.example.v1.post.generalForum.repository.GeneralForumRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final GeneralForumRepository generalForumRepository;

    public CommentService(CommentRepository commentRepository, MemberRepository memberRepository, GeneralForumRepository generalForumRepository) {
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
        this.generalForumRepository = generalForumRepository;
    }
    public CommentResponseDto createComment(String email, Long postId, CommentRequestDto commentRequestDto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
        GeneralForum generalForum = generalForumRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자유게시판 글을 찾을 수가 없습니다."));
        Comment comment = Comment.builder()
                .generalForum(generalForum)
                .writer(member)
                .content(commentRequestDto.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
        CommentResponseDto commentResponseDto = new CommentResponseDto(
                comment.getId(),
                comment.getWriter().getName(),
                comment.getContent(),
                commentRepository.countByGeneralForum(generalForum),
                comment.getCreatedAt(),
                comment.getGeneralForum().getId()
        );
        return commentResponseDto;
    }
    public List<CommentResponseDto> getCommentList(Long postId) {
        GeneralForum general = generalForumRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return commentRepository.findByGeneralForum(general).stream()
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getWriter().getName(),
                        comment.getContent(),
                        commentRepository.countByGeneralForum(general),
                        comment.getCreatedAt(),
                        comment.getGeneralForum().getId()
                ))
                .toList();
    }
    public Long countComment(Long postId) {
        GeneralForum generalForum = generalForumRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자유게시판 글을 찾을 수가 없습니다."));
        return commentRepository.countByGeneralForum(generalForum);
    }
    public List<CommentResponseDto> getMyComment(String email){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수가 없습니다."));
        return commentRepository.findByWriter(member).stream()
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getWriter().getName(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getGeneralForum().getId()
                ))
                .toList();
    }
}
