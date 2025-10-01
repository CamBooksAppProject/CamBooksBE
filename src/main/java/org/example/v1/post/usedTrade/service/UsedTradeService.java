package org.example.v1.post.usedTrade.service;

import jakarta.transaction.Transactional;
import org.example.v1.member.domain.Member;
import org.example.v1.member.repository.MemberRepository;
import org.example.v1.post.image.domain.PostImage;
import org.example.v1.post.image.repository.PostImageRepository;
import org.example.v1.post.usedTrade.domain.TradeStatusType;
import org.example.v1.post.usedTrade.domain.UsedTrade;
import org.example.v1.post.usedTrade.domain.UsedTradeStatus;
import org.example.v1.post.usedTrade.dto.UsedTradePreviewDto;
import org.example.v1.post.usedTrade.dto.UsedTradeRequestDto;
import org.example.v1.post.usedTrade.dto.UsedTradeResponseDto;
import org.example.v1.post.usedTrade.repository.UsedTradeRepository;
import org.example.v1.post.usedTrade.repository.UsedTradeStatusRepository;
import org.example.v1.postLike.repository.PostLikeRepository;
import org.example.v1.searchResult.dto.SearchResultDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsedTradeService {
    private final UsedTradeRepository usedTradeRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostImageRepository postImageRepository;
    private final UsedTradeStatusRepository usedTradeStatusRepository;

    public UsedTradeService(UsedTradeRepository usedTradeRepository, MemberRepository memberRepository, PostLikeRepository postLikeRepository, PostImageRepository postImageRepository, UsedTradeStatusRepository usedTradeStatusRepository) {
        this.usedTradeRepository = usedTradeRepository;
        this.memberRepository = memberRepository;
        this.postLikeRepository = postLikeRepository;
        this.postImageRepository = postImageRepository;
        this.usedTradeStatusRepository = usedTradeStatusRepository;
    }

    public UsedTradeResponseDto create(Long memberId, UsedTradeRequestDto dto, List<MultipartFile> images) {
        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        UsedTrade post = new UsedTrade(
                dto.getTitle(),
                writer,
                LocalDateTime.now(),
                dto.getContent(),
                dto.getPrice(),
                dto.getIsbn(),
                0,
                dto.getTradeMethod()
        );

        UsedTrade saved = usedTradeRepository.save(post);

        if (images != null) {
            for (MultipartFile image : images) {
                // 예시: 서버 로컬에 저장
                String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path savePath = Paths.get("upload/used-trade").resolve(filename);

                try {
                    Files.createDirectories(savePath.getParent());
                    image.transferTo(savePath);
                    // PostImage 엔터티에 저장
                    postImageRepository.save(new PostImage(saved, "/upload/used-trade/" + filename));
                } catch (IOException e) {
                    throw new RuntimeException("이미지 저장 실패", e);
                }
            }
        }

        UsedTradeStatus status = new UsedTradeStatus(saved, TradeStatusType.AVAILABLE);
        usedTradeStatusRepository.save(status);

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
                postLikeRepository.countByPost(post),
                saved.getWriter().getId(),
                status.getStatus(),
                saved.getIsbn()
        );
    }

    public UsedTradeResponseDto getById(Long postId) {
        UsedTrade post = usedTradeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않음"));
        UsedTradeStatus status = usedTradeStatusRepository.findByUsedTrade(post);

        List<PostImage> images = postImageRepository.findByUsedTrade(post);
        List<String> imageUrls = images.stream()
                .map(PostImage::getImageUrl)
                .toList();
        post.increaseView();
        usedTradeRepository.save(post);

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
                postLikeRepository.countByPost(post),
                imageUrls,
                post.getWriter().getId(),
                status.getStatus(),
                post.getIsbn()
        );
    }

    public List<UsedTradePreviewDto> getAll() {
        List<UsedTrade> posts = usedTradeRepository.findAll();
        return getUsedTradePreviewDto(posts);
    }

    public UsedTradeResponseDto update(Long postId, Long memberId, UsedTradeRequestDto dto) {
        UsedTrade post = usedTradeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않음"));
        UsedTradeStatus usedTradeStatus = usedTradeStatusRepository.findByUsedTrade(post);

        if (!post.getWriter().getId().equals(memberId)) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }
        usedTradeStatus.setStatus(dto.getStatus());
        post.updateFields(dto.getTitle(), dto.getContent(), dto.getPrice(), dto.getTradeMethod());

        UsedTradeStatus savedStatus = usedTradeStatusRepository.save(usedTradeStatus);
        UsedTrade saved = usedTradeRepository.save(post);

        return new UsedTradeResponseDto(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getPrice(),
                saved.getTradeMethod().toString(),
                saved.getViewCount(),
                saved.getWriter().getName(),
                saved.getWriter().getUniversity().getNameKo(),
                saved.getClass().getSimpleName(),
                postLikeRepository.countByPost(saved),
                saved.getWriter().getId(),
                savedStatus.getStatus(),
                saved.getIsbn()
        );
    }

    public List<UsedTradePreviewDto> getMyUsedTradeListByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        List<UsedTrade> myPosts = usedTradeRepository.findByWriter(member);

        return getUsedTradePreviewDto(myPosts);
    }


    private List<UsedTradePreviewDto> getUsedTradePreviewDto(List<UsedTrade> myPosts) {
        return myPosts.stream()
                .map(post -> {
                    String thumbnail = postImageRepository.findByUsedTrade(post).stream()
                            .findFirst()
                            .map(PostImage::getImageUrl)
                            .orElse(null);
                    TradeStatusType status = usedTradeStatusRepository.findByUsedTrade(post).getStatus();

                    return new UsedTradePreviewDto(
                            post.getId(),
                            post.getTitle(),
                            post.getPrice(),
                            post.getViewCount(),
                            post.getWriter().getUniversity().getNameKo(),
                            postLikeRepository.countByPost(post),
                            thumbnail,
                            status
                    );
                })
                .toList();
    }

    public List<SearchResultDto> searchByKeyword(String keyword) {
        return usedTradeRepository
                .findByTitleContaining(keyword)
                .stream()
                .map(post -> new SearchResultDto(
                        "usedTrade",
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getWriter().getName(),
                        post.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void deleteByPostId(String email, Long postId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
        UsedTrade usedTrade = usedTradeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 중고거래글 x"));
        if (!usedTrade.getWriter().getId().equals(member.getId())) {
            throw new IllegalArgumentException("작성자만 지울 수 있습니다.");
        }
        postLikeRepository.deleteAllByPost(usedTrade);
        usedTradeStatusRepository.deleteAllByUsedTrade(usedTrade);
        usedTradeRepository.delete(usedTrade);
    }
}
