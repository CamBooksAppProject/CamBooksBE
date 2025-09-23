package org.example.v1.comment.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.v1.comment.repository.CommentRepository;
import org.example.v1.comment.domain.Comment;
import org.example.v1.comment.dto.CommentRequestDto;
import org.example.v1.comment.dto.CommentResponseDto;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.notification.domain.Notification;
import org.example.v1.notification.domain.NotificationType;
import org.example.v1.notification.repository.NotificationRepository;
import org.example.v1.notification.repository.NotificationTypeRepository;
import org.example.v1.post.generalForum.domain.GeneralForum;
import org.example.v1.post.generalForum.repository.GeneralForumRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final GeneralForumRepository generalForumRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    public CommentService(CommentRepository commentRepository, MemberRepository memberRepository, GeneralForumRepository generalForumRepository, NotificationRepository notificationRepository, NotificationTypeRepository notificationTypeRepository) {
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
        this.generalForumRepository = generalForumRepository;
        this.notificationRepository = notificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
    }
    @Transactional
    public CommentResponseDto createComment(String email, Long postId, CommentRequestDto commentRequestDto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
        GeneralForum generalForum = generalForumRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자유게시판 글을 찾을 수가 없습니다."));
        Member postWriter = generalForum.getWriter();
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
                comment.getGeneralForum().getId(),
                comment.getWriter().getId()
        );
        NotificationType byId = notificationTypeRepository.findById(6L)
                .orElseThrow(() -> new EntityNotFoundException("해당 타입의 NotificationType이 없습니다."));

        Notification notification = Notification.builder()
                .notificationType(byId)
                .content(comment.getWriter().getNickname()+"님이 [" + generalForum.getTitle() + "] 게시글에 댓글을 남겼습니다.")
                .navigateId(generalForum.getId())
                .member(generalForum.getWriter())
                .build();
        notificationRepository.save(notification);
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
                        comment.getGeneralForum().getId(),
                        comment.getWriter().getId()
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
                        comment.getGeneralForum().getId(),
                        comment.getWriter().getId()
                ))
                .toList();
    }

    @Transactional
    public void deleteAllGeneralForum(Long postId) {
        GeneralForum generalForum = generalForumRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자유게시판 글이 존재하지 않습니다."));
        commentRepository.deleteAllByGeneralForum(generalForum);
    }

    @Transactional
    public void deleteComment(String email, Long commentId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수가 없습니다."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니댜."));
        if(!comment.getWriter().getId().equals(member.getId())) {
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }
        commentRepository.deleteById(commentId);
    }
}
