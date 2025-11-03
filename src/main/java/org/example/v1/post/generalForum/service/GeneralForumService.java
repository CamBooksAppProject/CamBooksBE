package org.example.v1.post.generalForum.service;

import jakarta.transaction.Transactional;
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
import java.util.stream.Collectors;

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
                generalForum.getWriter().getNickname(),
                generalForum.getCreatedAt(),
                postLikeRepository.countByPost(generalForum),
                commentService.getCommentList(generalForum.getId()),
                commentService.countComment(generalForum.getId()),
                generalForum.getWriter().getId()
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
                            post.getWriter().getNickname(),
                            post.getCreatedAt(),
                            postLikeRepository.countByPost(post),
                            commentService.countComment(post.getId())
                    );
                }).toList();
    }


    // 검색
    public List<GeneralForumPreviewDto> searchByKeyword(String keyword) {
        List<GeneralForum> results = generalForumRepository.findByTitleContainingOrContentContaining(keyword, keyword);

        return results.stream()
                .map(post -> new GeneralForumPreviewDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getWriter().getName(),
                        post.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
    @Transactional
    public void deleteByGeneralForumId(String email, Long generalForumId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));
        GeneralForum generalForum = generalForumRepository.findById(generalForumId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자유게시판 글이 존재하지 않습니다."));
        commentService.deleteAllGeneralForum(generalForum.getId());
        postLikeRepository.deleteAllByPost(generalForum);
        generalForumRepository.delete(generalForum);
    }

    public void update(String email, Long postId,GeneralForumRequestDto dto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));
        GeneralForum generalForum = generalForumRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자유게시판 글이 존재하지 않습니다."));
        if (!generalForum.getWriter().getId().equals(member.getId())) {
            throw new IllegalArgumentException("해당 자유게시판 글 작성자가 아닙니다.");
        }
        generalForum.update(dto.getTitle(), dto.getContent());
        generalForumRepository.save(generalForum);
    }

}
