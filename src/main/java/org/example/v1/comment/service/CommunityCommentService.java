package org.example.v1.comment.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.v1.comment.domain.Comment;
import org.example.v1.comment.domain.CommunityComment;
import org.example.v1.comment.dto.CommentRequestDto;
import org.example.v1.comment.dto.CommentResponseDto;
import org.example.v1.comment.repository.CommentRepository;
import org.example.v1.comment.repository.CommunityCommentRepository;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.notification.domain.Notification;
import org.example.v1.notification.domain.NotificationType;
import org.example.v1.notification.repository.NotificationRepository;
import org.example.v1.notification.repository.NotificationTypeRepository;
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
    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    public CommunityCommentService(CommunityCommentRepository communityCommentRepository, MemberRepository memberRepository, CommunityRepository communityRepository, NotificationRepository notificationRepository, NotificationTypeRepository notificationTypeRepository) {
        this.communityCommentRepository = communityCommentRepository;
        this.memberRepository = memberRepository;
        this.communityRepository = communityRepository;
        this.notificationRepository = notificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
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
                comment.getCommunity().getId(),
                comment.getWriter().getId()
        );
        NotificationType byId = notificationTypeRepository.findById(5L)
                .orElseThrow(() -> new EntityNotFoundException("해당 타입의 NotificationType이 없습니다."));

        Notification notification = Notification.builder()
                .notificationType(byId)
                .content(comment.getWriter().getNickname()+"님이 [" + commu.getTitle() + "] 모집글에 댓글을 남겼습니다.")
                .navigateId(commu.getId())
                .member(commu.getWriter())
                .build();
        notificationRepository.save(notification);
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
                        comment.getCommunity().getId(),
                        comment.getWriter().getId()
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
                        comment.getCommunity().getId(),
                        comment.getWriter().getId()
                ))
                .toList();
    }
    @Transactional
    public void deleteAllByCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("해당 커뮤니티가 없습니다."));
        communityCommentRepository.deleteAllByCommunity(community);
    }

    @Transactional
    public void deleteComment(String email, Long commentId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수가 없습니다."));
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));
        if(!member.getId().equals(comment.getWriter().getId())) {
            throw new IllegalArgumentException("댓글 작성자가 아닙니다.");
        }
        communityCommentRepository.delete(comment);
    }
}
