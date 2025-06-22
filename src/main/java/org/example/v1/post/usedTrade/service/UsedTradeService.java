package org.example.v1.post.usedTrade.service;

import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.usedTrade.domain.UsedTrade;
import org.example.v1.post.usedTrade.dto.UsedTradeRequestDto;
import org.example.v1.post.usedTrade.dto.UsedTradeResponseDto;
import org.example.v1.post.usedTrade.repository.UsedTradeRepository;
import org.example.v1.postLike.repository.PostLikeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsedTradeService {
    private final UsedTradeRepository usedTradeRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;

    public UsedTradeService(UsedTradeRepository usedTradeRepository, MemberRepository memberRepository, PostLikeRepository postLikeRepository) {
        this.usedTradeRepository = usedTradeRepository;
        this.memberRepository = memberRepository;
        this.postLikeRepository = postLikeRepository;
    }

    public UsedTradeResponseDto create(Long memberId, UsedTradeRequestDto dto) {
        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        UsedTrade post = new UsedTrade(
                dto.getTitle(),
                writer,
                LocalDateTime.now(),
                dto.getContent(),
                dto.getPrice(),
                0,
                dto.getTradeMethod()
        );

        UsedTrade saved = usedTradeRepository.save(post);

        return new UsedTradeResponseDto(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getPrice(),
                saved.getTradeMethod().toString(),
                saved.getViewCount(),
                saved.getWriter().getName(),
                saved.getWriter().getUniversity().toString(),
                saved.getClass().toString(),
                postLikeRepository.countByPost(post)
        );
    }

    public UsedTradeResponseDto getById(Long postId) {
        UsedTrade post = usedTradeRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않음"));
        return new UsedTradeResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getPrice(),
                post.getTradeMethod().toString(),
                post.getViewCount(),
                post.getWriter().getName(),
                post.getWriter().getUniversity().getNameKo(),
                post.getClass().getSimpleName(),
                postLikeRepository.countByPost(post)
        );
    }
    public List<UsedTradeResponseDto> getAll() {
        List<UsedTrade> posts = usedTradeRepository.findAll();
        return posts.stream()
                .map(post -> new UsedTradeResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getPrice(),
                        post.getTradeMethod().toString(),
                        post.getViewCount(),
                        post.getWriter().getName(),
                        post.getWriter().getUniversity().getNameKo(),
                        post.getClass().getSimpleName(),
                        postLikeRepository.countByPost(post)
                ))
                .toList();
    }

    public UsedTradeResponseDto update(Long postId, Long memberId, UsedTradeRequestDto dto) {
        UsedTrade post = usedTradeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않음"));

        if (!post.getWriter().getId().equals(memberId)) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        post.updateFields(dto.getTitle(), dto.getContent(), dto.getPrice(), dto.getTradeMethod());

        UsedTrade updated = usedTradeRepository.save(post);

        return new UsedTradeResponseDto(
                updated.getId(),
                updated.getTitle(),
                updated.getContent(),
                updated.getPrice(),
                updated.getTradeMethod().toString(),
                updated.getViewCount(),
                updated.getWriter().getName(),
                updated.getWriter().getUniversity().getNameKo(),
                updated.getClass().getSimpleName(),
                postLikeRepository.countByPost(updated)
        );
    }

    public void delete(Long memberId, Long postId) {
        UsedTrade post = usedTradeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않음"));

        if (!post.getWriter().getId().equals(memberId)) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        usedTradeRepository.delete(post);
    }
}
