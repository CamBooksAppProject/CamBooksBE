package org.example.v1.post.generalForum.service;

import org.example.v1.comment.dto.CommentResponseDto;
import org.example.v1.comment.service.CommentService;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.community.domain.Community;
import org.example.v1.post.community.dto.CommunityPreviewDto;
import org.example.v1.post.generalForum.domain.GeneralForum;
import org.example.v1.post.generalForum.dto.GeneralForumPreviewDto;
import org.example.v1.post.generalForum.dto.GeneralForumRequestDto;
import org.example.v1.post.generalForum.dto.GeneralForumResponseDto;
import org.example.v1.post.generalForum.repository.GeneralForumRepository;
import org.example.v1.post.image.domain.CommunityImage;
import org.example.v1.postLike.repository.PostLikeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GeneralForumService {
    private final GeneralForumRepository generalForumRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentService commentService;

    public GeneralForumService(GeneralForumRepository generalForumRepository, MemberRepository memberRepository, PostLikeRepository postLikeRepository, CommentService commentService) {
        this.generalForumRepository = generalForumRepository;
        this.memberRepository = memberRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentService = commentService;
    }
    public GeneralForumPreviewDto create(String email, GeneralForumRequestDto dto){
        Member writer = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        GeneralForum post = new GeneralForum(
                dto.getTitle(),
                writer,
                LocalDateTime.now(),
                dto.getContent()
        );
        generalForumRepository.save(post);
        return new GeneralForumPreviewDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getWriter().getName(),
                post.getCreatedAt()
        );
    }

    public List<GeneralForum> search(String keyword) {
        return generalForumRepository.findByTitleContaining(keyword);
    }

    public GeneralForumResponseDto getById(Long postId) {
        GeneralForum generalForum =  generalForumRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않음"));
        return new GeneralForumResponseDto(
                generalForum.getId(),
                generalForum.getTitle(),
                generalForum.getContent(),
                generalForum.getWriter().getName(),
                generalForum.getCreatedAt(),
                postLikeRepository.countByPost(generalForum),
                commentService.getCommentList(generalForum.getId()),
                commentService.countComment(generalForum.getId())
        );
    }
    public List<GeneralForumPreviewDto> getAll(){
        List<GeneralForum> generalForums = generalForumRepository.findAll();
        return getGeneralForumPreviewDtoList(generalForums);
    }
    private List<GeneralForumPreviewDto> getGeneralForumPreviewDtoList(List<GeneralForum> generalForums){
        return generalForums.stream()
                .map(post->{
                    return new GeneralForumPreviewDto(
                            post.getId(),
                            post.getTitle(),
                            post.getContent(),
                            post.getWriter().getName(),
                            post.getCreatedAt(),
                            postLikeRepository.countByPost(post),
                            commentService.countComment(post.getId())
                    );
                }).toList();
    }

}
